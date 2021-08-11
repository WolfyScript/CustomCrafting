package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeFurnace;

public class RecipeCacheFurnace extends RecipeCacheCooking<CustomRecipeFurnace> {

    protected RecipeCacheFurnace() {
        super();
    }

    protected RecipeCacheFurnace(CustomRecipeFurnace recipe) {
        super(recipe);
    }

    @Override
    protected CustomRecipeFurnace constructRecipe() {
        return create(new CustomRecipeFurnace(key));
    }
}
