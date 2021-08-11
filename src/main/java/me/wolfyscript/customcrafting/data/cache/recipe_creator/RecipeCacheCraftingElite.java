package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CraftingRecipeEliteShaped;
import me.wolfyscript.customcrafting.recipes.CraftingRecipeEliteShapeless;
import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;

public class RecipeCacheCraftingElite extends RecipeCacheCraftingAbstract<EliteRecipeSettings> {

    public RecipeCacheCraftingElite() {
        super();
    }

    public RecipeCacheCraftingElite(CraftingRecipe<?, EliteRecipeSettings> recipe) {
        super(recipe);
    }

    @Override
    protected CraftingRecipe<?, EliteRecipeSettings> constructRecipe() {
        return create(shapeless ? new CraftingRecipeEliteShapeless(key) : new CraftingRecipeEliteShaped(key));
    }
}
