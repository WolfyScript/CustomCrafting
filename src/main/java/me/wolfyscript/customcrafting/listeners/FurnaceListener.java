package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.RandomCollection;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FurnaceListener implements Listener {

    private final CustomCrafting customCrafting;
    private final List<InventoryType> invs = Arrays.asList(InventoryType.FURNACE, InventoryType.BLAST_FURNACE, InventoryType.SMOKER);

    public FurnaceListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
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
                    for (CustomItem customItem : Registry.CUSTOM_ITEMS.values()) {
                        if (customItem.getBurnTime() > 0) {
                            if (customItem.isSimilar(input)) {
                                if (customItem.getAllowedBlocks().contains(material)) {
                                    if (event.getClick().equals(ClickType.LEFT)) {
                                        ItemStack fuel = furnaceInventory.getFuel();
                                        if (fuel != null && !fuel.getType().equals(Material.AIR)) {
                                            if (fuel.isSimilar(input)) {
                                                event.setCancelled(true);
                                                int possibleAmount = fuel.getMaxStackSize() - fuel.getAmount();
                                                fuel.setAmount(fuel.getAmount() + (Math.min(input.getAmount(), possibleAmount)));
                                                input.setAmount(input.getAmount() - possibleAmount);
                                                Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                                                    furnaceInventory.setFuel(fuel);
                                                    event.getWhoClicked().setItemOnCursor(input);
                                                }, 1);
                                            } else {
                                                if (customItem.getItemStack().getType().isFuel()) {
                                                    event.setCancelled(true);
                                                }
                                                Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                                                    event.getWhoClicked().setItemOnCursor(furnaceInventory.getFuel());
                                                    furnaceInventory.setFuel(input);
                                                }, 1);
                                            }
                                        } else {
                                            if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                                                if (customItem.getItemStack().getType().isFuel()) {
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
                                                if (customItem.getItemStack().getType().isFuel()) {
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
        for (CustomItem customItem : Registry.CUSTOM_ITEMS.values()) {
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
        Furnace furnace = (Furnace) event.getBlock().getState();
        FurnaceInventory inventory = furnace.getInventory();
        ItemStack currentResultItem = furnace.getInventory().getResult();
        final Class<?> type;
        switch (furnace.getType()) {
            case BLAST_FURNACE:
                type = BlastingRecipe.class;
                break;
            case SMOKER:
                type = SmokingRecipe.class;
                break;
            default:
                type = FurnaceRecipe.class;
        }
        List<Recipe> recipes = Bukkit.getRecipesFor(event.getResult()).stream().filter(recipe -> type.isInstance(recipe) && recipe.getResult().isSimilar(event.getResult())).collect(Collectors.toList());
        for (Recipe recipe : recipes) {
            if (!(recipe instanceof Keyed)) continue;
            if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(((Keyed) recipe).getKey().toString())) {
                event.setCancelled(true);
                continue;
            }
            CustomCookingRecipe<?, ?> customRecipe = (CustomCookingRecipe<?, ?>) customCrafting.getRecipeHandler().getRecipe(NamespacedKey.getByString(((Keyed) recipe).getKey().toString()));
            if (isRecipeValid(event.getBlock().getType(), customRecipe)) {
                if (customRecipe.getConditions().checkConditions(customRecipe, new Conditions.Data(null, event.getBlock(), null))) {
                    event.setCancelled(false);
                    if (customRecipe.getResults().size() > 1) {
                        RandomCollection<CustomItem> items = new RandomCollection<>();
                        customRecipe.getResults().forEach(item -> items.add(item.getRarityPercentage(), item));
                        if (!items.isEmpty()) {
                            CustomItem item = items.next();
                            if (currentResultItem == null) {
                                event.setResult(item.create());
                                break;
                            }
                            int nextAmount = currentResultItem.getAmount() + item.getAmount();
                            if ((item.isSimilar(currentResultItem)) && nextAmount <= currentResultItem.getMaxStackSize() && !ItemUtils.isAirOrNull(inventory.getSmelting())) {
                                inventory.getSmelting().setAmount(inventory.getSmelting().getAmount() - 1);
                                currentResultItem.setAmount(nextAmount);
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

    private boolean isRecipeValid(Material furnaceType, CustomRecipe recipe) {
        if (recipe instanceof CustomCookingRecipe) {
            switch (furnaceType) {
                case BLAST_FURNACE:
                    return recipe instanceof CustomBlastRecipe;
                case SMOKER:
                    return recipe instanceof CustomSmokerRecipe;
                case FURNACE:
                    return recipe instanceof CustomFurnaceRecipe;
            }
        }
        return false;
    }


}
