package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeCampfire;

public class RecipeCacheCampfire extends RecipeCacheCooking<CustomRecipeCampfire> {

    RecipeCacheCampfire() {
        super();
    }

    RecipeCacheCampfire(CustomRecipeCampfire recipe) {
        super(recipe);
    }

    @Override
    protected CustomRecipeCampfire constructRecipe() {
        return create(new CustomRecipeCampfire(key));
    }
}
