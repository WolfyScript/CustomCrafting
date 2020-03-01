package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import me.wolfyscript.utilities.api.utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BrewingStandListener implements Listener {

    private WolfyUtilities api;
    private List<Location> activeBrewingStands = new ArrayList<>();

    public BrewingStandListener() {
        this.api = WolfyUtilities.getAPI(CustomCrafting.getInst());
    }


    @EventHandler
    public void onTest(BrewingStandFuelEvent event) {

    }

    @EventHandler
    public void onInv(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof BrewerInventory) {
            BrewerInventory inventory = (BrewerInventory) event.getClickedInventory();
            Player player = (Player) event.getWhoClicked();
            Location location = inventory.getLocation();

            if (event.getSlot() != 4) {
                final ItemStack cursor = event.getCursor(); //And the item in the cursor
                final ItemStack currentItem = event.getCurrentItem(); //We want to get the item in the slot
                //Place items
                if (event.getClickedInventory() == null) return;
                if (event.getClickedInventory().getType() != InventoryType.BREWING) return;

                if (event.isRightClick()) {
                    //Dropping one item or pick up half
                    if (event.getAction().equals(InventoryAction.PICKUP_HALF) || event.getAction().equals(InventoryAction.PICKUP_SOME))
                        return;
                    //Dropping one item
                    if (ItemUtils.isAirOrNull(currentItem)) {
                        event.setCancelled(true);
                        cursor.setAmount(cursor.getAmount() - 1);
                        ItemStack itemStack = new ItemStack(cursor);
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                            itemStack.setAmount(1);
                            inventory.setItem(event.getSlot(), itemStack);
                            event.getWhoClicked().setItemOnCursor(cursor);
                        }, 1);
                    } else if (currentItem.isSimilar(cursor) || cursor.isSimilar(currentItem)) {
                        if (currentItem.getAmount() < currentItem.getMaxStackSize()) {
                            if (cursor.getAmount() > 0) {
                                event.setCancelled(true);
                                currentItem.setAmount(currentItem.getAmount() + 1);
                                cursor.setAmount(cursor.getAmount() - 1);
                                player.updateInventory();
                            }
                        }
                    }
                } else {
                    //Placing an item
                    if (ItemUtils.isAirOrNull(event.getCursor())) {
                        return; //Make sure cursor contains item
                    }
                    if (!ItemUtils.isAirOrNull(currentItem)) {
                        if (currentItem.isSimilar(cursor) || cursor.isSimilar(currentItem)) {
                            event.setCancelled(true);
                            int possibleAmount = currentItem.getMaxStackSize() - currentItem.getAmount();
                            currentItem.setAmount(currentItem.getAmount() + (cursor.getAmount() < possibleAmount ? cursor.getAmount() : possibleAmount));
                            cursor.setAmount(cursor.getAmount() - possibleAmount);
                        } else {
                            if (!ItemUtils.isAirOrNull(cursor)) {
                                event.setCancelled(true);
                                ItemStack itemStack = new ItemStack(cursor);
                                event.getView().setCursor(currentItem);
                                inventory.setItem(event.getSlot(), itemStack);
                            }
                        }
                    } else if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                        ItemStack itemStack = new ItemStack(cursor);
                        Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                            inventory.setItem(event.getSlot(), itemStack);
                            event.getView().setCursor(new ItemStack(Material.AIR));
                        });
                    }
                    player.updateInventory();//And we update the inventory
                }
            }

            if (event.getSlot() == 3) {
                //Recipe Checker!
                final CustomItem potion0 = CustomItem.getByItemStack(inventory.getItem(0));
                final CustomItem potion1 = CustomItem.getByItemStack(inventory.getItem(1));
                final CustomItem potion2 = CustomItem.getByItemStack(inventory.getItem(2));

                BrewingStand brewingStand = inventory.getHolder();

                Method getTileEntity = Reflection.getMethod(Reflection.getOBC("block.CraftBrewingStand"), "getTileEntity");
                Field brewTime = Reflection.getField(Reflection.getNMS("TileEntityBrewingStand"), "brewTime");
                Field fuelLevel = Reflection.getField(Reflection.getNMS("TileEntityBrewingStand"), "fuelLevel");
                getTileEntity.setAccessible(true);
                brewTime.setAccessible(true);

                if (!activeBrewingStands.contains(location)) {
                    if (brewingStand.getFuelLevel() > 0) {
                        try {
                            Object tileEntityObj = getTileEntity.invoke(brewingStand);
                            brewTime.setInt(tileEntityObj, 400);
                            fuelLevel.setInt(tileEntityObj, fuelLevel.getInt(tileEntityObj) - 1);
                            activeBrewingStands.add(location);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            return;
                        }
                        AtomicInteger tick = new AtomicInteger(400);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (tick.get() > 0) {
                                    try {
                                        Object tileEntityObj = getTileEntity.invoke(brewingStand);
                                        brewTime.setInt(tileEntityObj, tick.decrementAndGet());
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                        cancel();
                                    }
                                } else {
                                    System.out.println("Finished");
                                    activeBrewingStands.remove(location);
                                    cancel();
                                }
                            }
                        }.runTaskTimerAsynchronously(CustomCrafting.getInst(), 2, 1);
                    }
                }
            }
        }
    }


}
