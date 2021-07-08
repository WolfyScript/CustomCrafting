package me.wolfyscript.customcrafting.recipes.data;

import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;

public class FurnaceRecipeData extends CookingRecipeData<CustomFurnaceRecipe> {

    public FurnaceRecipeData(CustomFurnaceRecipe recipe, IngredientData source) {
        super(recipe, source);
    }
}
