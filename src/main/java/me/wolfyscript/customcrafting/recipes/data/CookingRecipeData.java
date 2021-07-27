package me.wolfyscript.customcrafting.recipes.data;

import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;

import java.util.Map;

public abstract class CookingRecipeData<R extends CustomCookingRecipe<?, ?>> extends RecipeData<R> {

    protected CookingRecipeData(R recipe, IngredientData source) {
        super(recipe, Map.of(source.recipeSlot(), source));
    }
}
