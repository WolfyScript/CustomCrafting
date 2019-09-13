package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;

public class FurnaceListener implements Listener {

    List<InventoryType> invs = new ArrayList<>();

    public FurnaceListener() {
        invs.add(InventoryType.FURNACE);
        if (WolfyUtilities.hasVillagePillageUpdate()) {
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
                                                if (customItem.getType().isFuel()) {
                                                    event.setCancelled(true);
                                                }
                                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                                    event.getWhoClicked().setItemOnCursor(furnaceInventory.getFuel());
                                                    furnaceInventory.setFuel(input);
                                                }, 1);
                                            }
                                        } else {
                                            if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                                                if (customItem.getType().isFuel()) {
                                                    event.setCancelled(true);
                                                }
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
                                            } else {
                                                //TODO: Switch Cursor with Fuel!
                                                if (customItem.getType().isFuel()) {
                                                    event.setCancelled(true);
                                                }
                                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                                    event.getWhoClicked().setItemOnCursor(furnaceInventory.getFuel());
                                                    furnaceInventory.setFuel(input);
                                                }, 1);
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
                                } else {
                                    event.setCancelled(true);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /*
    @EventHandler
    public void onMove(InventoryMoveItemEvent event) {
        System.out.println("MOVE "+event.isCancelled());
        if (invs.contains(event.getDestination().getType())) {
            Material material = Material.valueOf(event.getDestination().getType().toString());
            ItemStack input = event.getItem();

            for (CustomItem customItem : CustomCrafting.getRecipeHandler().getCustomItems()) {
                if (customItem.getBurnTime() > 0) {
                    if (customItem.isSimilar(input)) {
                        if(event.getDestination().getLocation() != null && event.getSource().getLocation() != null){
                            Location locHopper = event.getSource().getLocation();
                            Location locFurnace = event.getDestination().getLocation();
                            if(!(locHopper.getBlockY() > locFurnace.getBlockY()) && !(locHopper.getBlockY() < locFurnace.getBlockY())){
                                if (customItem.getAllowedBlocks().contains(material)) {
                                    if(!customItem.getType().isFuel()){
                                        ItemStack itemStack = event.getItem().clone();
                                        if(event.getDestination().getItem(1) == null || event.getDestination().getItem(1).getType().equals(Material.AIR)){
                                            event.setCancelled(true);
                                            event.getDestination().setItem(1, itemStack);
                                        }else if(customItem.isSimilar(event.getDestination().getItem(1))){
                                            event.getDestination().getItem(1).setAmount(event.getDestination().getItem(1).getAmount()+1);
                                            event.getSource().removeItem(itemStack);
                                        }
                                    }
                                }else{
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    */

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

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        List<Recipe> recipes = Bukkit.getRecipesFor(event.getResult());
        for (Recipe recipe : recipes) {
            if(recipe.getResult().isSimilar(event.getResult())){
                CustomRecipe customRecipe = CustomCrafting.getRecipeHandler().getRecipe(((Keyed) recipe).getKey().toString());
                if (isRecipeValid(event.getBlock().getType(), customRecipe)) {
                    RandomCollection<CustomItem> items = new RandomCollection<>();
                    for (CustomItem customItem : customRecipe.getCustomResults()) {
                        items.add(customItem.getRarityPercentage(), customItem);
                    }
                    if(!items.isEmpty()){
                        event.setResult(items.next());
                    }
                    break;
                }
            }
        }

    }

    private boolean isRecipeValid(Material furnaceType, CustomRecipe recipe) {
        if (recipe instanceof CustomCookingRecipe) {
            switch (furnaceType) {
                case BLAST_FURNACE:
                    return recipe instanceof BlastingRecipe;
                case SMOKER:
                    return recipe instanceof SmokingRecipe;
                case FURNACE:
                    return recipe instanceof FurnaceRecipe;
            }
        }
        return false;
    }
}
