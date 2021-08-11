package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CraftingRecipeShaped;
import me.wolfyscript.customcrafting.recipes.CraftingRecipeShapeless;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;

public class RecipeCacheCrafting extends RecipeCacheCraftingAbstract<AdvancedRecipeSettings> {

    public RecipeCacheCrafting() {
        super();
    }

    public RecipeCacheCrafting(CraftingRecipe<?, AdvancedRecipeSettings> recipe) {
        super(recipe);
    }

    @Override
    protected CraftingRecipe<?, AdvancedRecipeSettings> constructRecipe() {
        return create(shapeless ? new CraftingRecipeShapeless(key) : new CraftingRecipeShaped(key));
    }

}
