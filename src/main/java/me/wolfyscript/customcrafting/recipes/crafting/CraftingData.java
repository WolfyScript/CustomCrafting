package me.wolfyscript.customcrafting.recipes.crafting;

import com.sun.javafx.geom.Vec2d;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

import java.util.HashMap;

public class CraftingData {

    CraftingRecipe<CraftConfig> recipe;
    HashMap<Vec2d, CustomItem> foundItems;

    public CraftingData(CraftingRecipe<CraftConfig> recipe, HashMap<Vec2d, CustomItem> foundItems) {
        this.recipe = recipe;
        this.foundItems = foundItems;

    }

    public CraftingRecipe<CraftConfig> getRecipe() {
        return recipe;
    }

    public HashMap<Vec2d, CustomItem> getFoundItems() {
        return foundItems;
    }
}
