package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeSmoking;

public class RecipeCacheSmoking extends RecipeCacheCooking<CustomRecipeSmoking> {

    RecipeCacheSmoking() {
        super();
    }

    RecipeCacheSmoking(CustomRecipeSmoking recipe) {
        super(recipe);
    }

    @Override
    protected CustomRecipeSmoking constructRecipe() {
        return create(new CustomRecipeSmoking(key));
    }
}
