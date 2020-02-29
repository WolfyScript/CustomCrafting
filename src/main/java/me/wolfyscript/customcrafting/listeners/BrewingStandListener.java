package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.ItemUtils;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BrewingStandListener implements Listener {

    private WolfyUtilities api;
    private HashMap<Location, Boolean> updateBrewingStands = new HashMap<>();
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
            InventoryAction action = event.getAction();
            Location location = inventory.getLocation();
            System.out.println("Event is called! " + event.getSlot() + " Action: " + action);

            updateBrewingStands.put(location, true);
            if (event.getSlot() != 4) {
                final ItemStack cursor = event.getCursor(); //And the item in the cursor
                final ItemStack currentItem = event.getCurrentItem(); //We want to get the item in the slot
                //Place items
                if (event.getClickedInventory() == null) {
                    updateBrewingStands.put(location, false);
                    return;
                }
                if (event.getClickedInventory().getType() != InventoryType.BREWING) {
                    updateBrewingStands.put(location, false);
                    return;
                }

                if (event.isRightClick()) {
                    //Dropping one item or pick up half
                    if (event.getAction().equals(InventoryAction.PICKUP_HALF) || event.getAction().equals(InventoryAction.PICKUP_SOME)) {
                        updateBrewingStands.put(location, false);
                        return;
                    }
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
                        updateBrewingStands.put(location, false);
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
            updateBrewingStands.put(location, false);

            if (event.getSlot() == 3) {
                //Recipe Checker!
                final CustomItem potion0 = CustomItem.getByItemStack(inventory.getItem(0));
                final CustomItem potion1 = CustomItem.getByItemStack(inventory.getItem(1));
                final CustomItem potion2 = CustomItem.getByItemStack(inventory.getItem(2));

                BrewingStand brewingStand = (BrewingStand) location.getBlock().getState();

                if (!activeBrewingStands.contains(location)) {
                    if (brewingStand.getFuelLevel() > 0) {
                        brewingStand.setFuelLevel(brewingStand.getFuelLevel() - 1);
                        //Start brewing
                        brewingStand.setBrewingTime(400);
                        brewingStand.update();

                        activeBrewingStands.add(location);
                        BukkitTask task = Bukkit.getScheduler().runTaskTimer(CustomCrafting.getInst(), () -> {
                            if (brewingStand.getBrewingTime() > 0) {
                                brewingStand.setBrewingTime(brewingStand.getBrewingTime() - 1);
                                if (!updateBrewingStands.get(location)) {
                                    brewingStand.update();
                                }
                            } else {
                                System.out.println("Finished");
                            }
                        }, 2, 1);
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                            task.cancel();
                            activeBrewingStands.remove(location);
                        }, 403);
                    }
                }
            }
        }
    }


}
