package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cache.Furnace;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FurnaceListener implements Listener {

    List<InventoryType> invs = new ArrayList<>();

    public FurnaceListener(){
        invs.add(InventoryType.FURNACE);
        if(WolfyUtilities.hasVillagePillageUpdate()){
            invs.add(InventoryType.BLAST_FURNACE);
            invs.add(InventoryType.SMOKER);
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && invs.contains(event.getClickedInventory().getType())) {
            FurnaceInventory furnaceInventory = (FurnaceInventory) event.getClickedInventory();
            if (event.getSlot() == 1) {
                Material material = Material.FURNACE;
                if (event.getWhoClicked().getTargetBlockExact(6) != null) {
                    material = event.getWhoClicked().getTargetBlockExact(6).getType();
                }
                ItemStack input = event.getCursor();
                if (input != null) {
                    for (CustomItem customItem : CustomCrafting.getRecipeHandler().getCustomItems()) {
                        if (customItem.getBurnTime() > 0) {
                            if (customItem.isSimilar(input)) {
                                if (customItem.getAllowedBlocks().contains(material)) {
                                    if (event.getClick().equals(ClickType.LEFT)) {
                                        ItemStack fuel = furnaceInventory.getFuel();
                                        if (fuel != null && !fuel.getType().equals(Material.AIR)) {
                                            if (fuel.isSimilar(input)) {
                                                event.setCancelled(true);
                                                int possibleAmount = fuel.getMaxStackSize() - fuel.getAmount();
                                                fuel.setAmount(fuel.getAmount() + (input.getAmount() < possibleAmount ? input.getAmount() : possibleAmount));
                                                input.setAmount(input.getAmount() - possibleAmount);
                                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                                    furnaceInventory.setFuel(fuel);
                                                    event.getWhoClicked().setItemOnCursor(input);
                                                }, 1);
                                            } else {
                                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                                    event.getWhoClicked().setItemOnCursor(furnaceInventory.getFuel());
                                                    furnaceInventory.setFuel(input);
                                                }, 1);
                                            }
                                        } else {
                                            if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                                    event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
                                                    furnaceInventory.setFuel(input);
                                                }, 1);
                                            }
                                        }
                                    } else {
                                        ItemStack fuel = furnaceInventory.getFuel();
                                        if (fuel != null && !fuel.getType().equals(Material.AIR)) {
                                            if (fuel.isSimilar(input)) {
                                                if (fuel.getAmount() < fuel.getMaxStackSize()) {
                                                    if (input.getAmount() > 0) {
                                                        event.setCancelled(true);
                                                        fuel.setAmount(fuel.getAmount() + 1);
                                                        input.setAmount(input.getAmount() - 1);
                                                        event.getWhoClicked().setItemOnCursor(input);
                                                    }
                                                }
                                            }
                                        } else {
                                            event.setCancelled(true);
                                            input.setAmount(input.getAmount() - 1);
                                            Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                                ItemStack itemStack = new ItemStack(input);
                                                itemStack.setAmount(1);
                                                furnaceInventory.setFuel(itemStack);
                                                event.getWhoClicked().setItemOnCursor(input);
                                            }, 1);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        ItemStack input = event.getFuel();
        for (CustomItem customItem : CustomCrafting.getRecipeHandler().getCustomItems()) {
            if (customItem.getBurnTime() > 0) {
                if (customItem.isSimilar(input)) {
                    if (customItem.getAllowedBlocks().contains(event.getBlock().getType())) {
                        event.setCancelled(false);
                        event.setBurning(true);
                        event.setBurnTime(customItem.getBurnTime());
                        break;
                    }
                }
            }
        }
    }
}
