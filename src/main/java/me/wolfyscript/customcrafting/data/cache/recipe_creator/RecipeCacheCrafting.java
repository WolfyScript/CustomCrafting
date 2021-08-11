package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CraftingRecipeShaped;
import me.wolfyscript.customcrafting.recipes.CraftingRecipeShapeless;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;

public class RecipeCacheCrafting extends RecipeCacheCraftingAbstract<AdvancedRecipeSettings> {

    public RecipeCacheCrafting(RecipeCreatorCache creatorCache) {
        super(creatorCache);
    }

    public RecipeCacheCrafting(RecipeCreatorCache creatorCache, CraftingRecipe<?, AdvancedRecipeSettings> recipe) {
        super(creatorCache, recipe);
    }

    @Override
    protected CraftingRecipe<?, AdvancedRecipeSettings> constructRecipe() {
        return create(shapeless ? new CraftingRecipeShapeless(key) : new CraftingRecipeShaped(key));
    }

}
