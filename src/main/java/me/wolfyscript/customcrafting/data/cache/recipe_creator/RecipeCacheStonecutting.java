package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeStonecutter;
import me.wolfyscript.customcrafting.recipes.recipe_item.Ingredient;

public class RecipeCacheStonecutting extends RecipeCache<CustomRecipeStonecutter> {

    private Ingredient source;

    public RecipeCacheStonecutting() {
        super();
    }

    public RecipeCacheStonecutting(CustomRecipeStonecutter recipe) {
        super(recipe);
        this.source = recipe.getSource().clone();
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        setSource(ingredient);
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return getSource();
    }

    @Override
    protected CustomRecipeStonecutter constructRecipe() {
        return null;
    }

    public Ingredient getSource() {
        return source;
    }

    public void setSource(Ingredient source) {
        this.source = source;
    }
}
