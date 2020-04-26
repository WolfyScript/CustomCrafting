package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CookingConfig;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.utils.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;

public class FurnaceListener implements Listener {

    private CustomCrafting customCrafting;
    private List<InventoryType> invs = new ArrayList<>();

    public FurnaceListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
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
            if (event.getSlotType().equals(InventoryType.SlotType.FUEL)) {
                Material material = Material.FURNACE;
                if (event.getWhoClicked().getTargetBlockExact(6) != null) {
                    material = event.getWhoClicked().getTargetBlockExact(6).getType();
                }
                ItemStack input = event.getCursor();
                if (input != null) {
                    for (CustomItem customItem : CustomItems.getCustomItems()) {
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
                                                Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                                                    furnaceInventory.setFuel(fuel);
                                                    event.getWhoClicked().setItemOnCursor(input);
                                                }, 1);
                                            } else {
                                                if (customItem.getType().isFuel()) {
                                                    event.setCancelled(true);
                                                }
                                                Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                                                    event.getWhoClicked().setItemOnCursor(furnaceInventory.getFuel());
                                                    furnaceInventory.setFuel(input);
                                                }, 1);
                                            }
                                        } else {
                                            if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                                                if (customItem.getType().isFuel()) {
                                                    event.setCancelled(true);
                                                }
                                                Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
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
                                                Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                                                    event.getWhoClicked().setItemOnCursor(furnaceInventory.getFuel());
                                                    furnaceInventory.setFuel(input);
                                                }, 1);
                                            }
                                        } else {
                                            event.setCancelled(true);
                                            input.setAmount(input.getAmount() - 1);
                                            Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
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

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        ItemStack input = event.getFuel();
        for (CustomItem customItem : CustomItems.getCustomItems()) {
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
            if (recipe.getResult().isSimilar(event.getResult())) {
                Furnace furnace = (Furnace) event.getBlock().getState();
                FurnaceInventory inventory = furnace.getInventory();
                ItemStack currentResultItem = furnace.getInventory().getResult();

                if (recipe instanceof Keyed && customCrafting.getRecipeHandler().getDisabledRecipes().contains(((Keyed) recipe).getKey().toString())) {
                    event.setCancelled(true);
                    continue;
                }
                CustomCookingRecipe<CookingConfig> customRecipe = (CustomCookingRecipe<CookingConfig>) customCrafting.getRecipeHandler().getRecipe(((Keyed) recipe).getKey().toString());
                if (isRecipeValid(event.getBlock().getType(), customRecipe)) {
                    if (customRecipe.getConditions().checkConditions(customRecipe, new Conditions.Data(null, event.getBlock(), null))) {
                        event.setCancelled(false);
                        if (customRecipe.getCustomResults().size() > 1) {
                            RandomCollection<CustomItem> items = new RandomCollection<>();
                            for (CustomItem customItem : customRecipe.getCustomResults()) {
                                items.add(customItem.getRarityPercentage(), customItem);
                            }
                            if (!items.isEmpty()) {
                                CustomItem item = items.next();
                                if (currentResultItem == null) {
                                    event.setResult(item);
                                    break;
                                }
                                int nextAmount = currentResultItem.getAmount() + item.getAmount();
                                if ((item.isSimilar(currentResultItem) || currentResultItem.isSimilar(item)) && nextAmount <= currentResultItem.getMaxStackSize()) {
                                    inventory.getSmelting().setAmount(inventory.getSmelting().getAmount() - 1);
                                    inventory.getResult().setAmount(nextAmount);
                                }
                                event.setCancelled(true);
                            }
                        }
                        break;
                    } else {
                        event.setCancelled(true);
                    }
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
