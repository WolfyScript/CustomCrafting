package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

import java.util.Map;

public class CraftingData {

    CraftingRecipe<?> recipe;
    Map<Vec2d, CustomItem> foundItems;

    public CraftingData(CraftingRecipe<?> recipe, Map<Vec2d, CustomItem> foundItems) {
        this.recipe = recipe;
        this.foundItems = foundItems;
    }

    public CraftingRecipe<?> getRecipe() {
        return recipe;
    }

    public Map<Vec2d, CustomItem> getFoundItems() {
        return foundItems;
    }
}
