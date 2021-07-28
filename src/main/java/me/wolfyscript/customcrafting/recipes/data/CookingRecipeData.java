package me.wolfyscript.customcrafting.recipes.data;

import me.wolfyscript.customcrafting.recipes.CustomRecipeCooking;

import java.util.Map;

public abstract class CookingRecipeData<R extends CustomRecipeCooking<?, ?>> extends RecipeData<R> {

    protected CookingRecipeData(R recipe, IngredientData source) {
        super(recipe, Map.of(source.recipeSlot(), source));
    }
}
