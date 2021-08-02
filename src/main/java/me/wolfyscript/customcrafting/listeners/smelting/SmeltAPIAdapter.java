package me.wolfyscript.customcrafting.listeners.smelting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.recipes.CustomRecipeBlasting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import me.wolfyscript.customcrafting.recipes.CustomRecipeFurnace;
import me.wolfyscript.customcrafting.recipes.CustomRecipeSmoking;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.data.BlastingRecipeData;
import me.wolfyscript.customcrafting.recipes.data.FurnaceRecipeData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.SmokerRecipeData;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public abstract class SmeltAPIAdapter {

    protected CustomCrafting customCrafting;

    protected SmeltAPIAdapter(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    public abstract void process(FurnaceSmeltEvent event, Block block, Furnace furnace, FurnaceInventory inventory, ItemStack currentResultItem);

    protected boolean processRecipe(FurnaceSmeltEvent event, NamespacedKey recipeKey, Block block, FurnaceInventory inventory, ItemStack currentResultItem) {
        if (Registry.RECIPES.get(recipeKey) instanceof CustomRecipeCooking<?, ?> cookingRecipe && cookingRecipe.validType(block.getType())) {
            if (cookingRecipe.checkConditions(new Conditions.Data(null, block, null))) {
                event.setCancelled(false);
                Result result = cookingRecipe.getResult();
                ItemStack source = event.getSource();
                Optional<CustomItem> customSource = cookingRecipe.getSource().check(source, cookingRecipe.isExactMeta());
                if (customSource.isPresent()) {
                    return applyResult(event, cookingRecipe, result, customSource.get(), block, inventory, currentResultItem);
                } else {
                    event.setCancelled(true);
                    return false;
                }
            }
            event.setCancelled(true);
        }
        return false;
    }

    private boolean applyResult(FurnaceSmeltEvent event, CustomRecipeCooking<?, ?> cookingRecipe, Result result, CustomItem customSource, Block block, FurnaceInventory inventory, ItemStack currentResultItem) {
        var data = new IngredientData(0, cookingRecipe.getSource(), customSource, event.getSource());
        ItemStack itemResult = result.getItem(switch (cookingRecipe.getRecipeType().getType()) {
            case FURNACE -> new FurnaceRecipeData((CustomRecipeFurnace) cookingRecipe, data);
            case SMOKER -> new SmokerRecipeData((CustomRecipeSmoking) cookingRecipe, data);
            case BLAST_FURNACE -> new BlastingRecipeData((CustomRecipeBlasting) cookingRecipe, data);
            default -> null;
        }, null, block);
        if (currentResultItem != null) {
            if (!itemResult.isSimilar(currentResultItem)) {
                event.setCancelled(true);
            } else {
                int nextAmount = currentResultItem.getAmount() + itemResult.getAmount();
                if (nextAmount <= currentResultItem.getMaxStackSize() && !ItemUtils.isAirOrNull(inventory.getSmelting())) {
                    inventory.getSmelting().setAmount(inventory.getSmelting().getAmount() - 1);
                    currentResultItem.setAmount(nextAmount);
                    result.executeExtensions(block.getLocation(), true, null);
                    result.removeCachedItem(block);
                }
            }
        } else {
            event.setResult(itemResult);
            result.executeExtensions(block.getLocation(), true, null);
            result.removeCachedItem(block);
        }
        return true;
    }

}
