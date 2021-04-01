package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CraftingData {

    CraftingRecipe<?> recipe;
    Result<?> result;
    Map<Vec2d, CustomItem> foundItems;

    public CraftingData(CraftingRecipe<?> recipe, Map<Vec2d, CustomItem> foundItems, ItemStack[] matrix) {
        this.recipe = recipe;
        this.result = recipe.getResult().get(matrix);
        this.foundItems = foundItems;
    }

    public CraftingRecipe<?> getRecipe() {
        return recipe;
    }

    public Map<Vec2d, CustomItem> getFoundItems() {
        return foundItems;
    }

    public Result<?> getResult() {
        return result;
    }

    public void setResult(Result<?> result) {
        this.result = result;
    }
}
