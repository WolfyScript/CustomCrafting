package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeFurnace;

public class RecipeCacheFurnace extends RecipeCacheCooking<CustomRecipeFurnace> {

    RecipeCacheFurnace() {
        super();
    }

    RecipeCacheFurnace(CustomRecipeFurnace recipe) {
        super(recipe);
    }

    @Override
    protected CustomRecipeFurnace constructRecipe() {
        return create(new CustomRecipeFurnace(key));
    }
}
