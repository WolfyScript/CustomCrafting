package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeStonecutter;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;

public class RecipeCacheStonecutting extends RecipeCache<CustomRecipeStonecutter> {

    private Ingredient source;

    RecipeCacheStonecutting() {
        super();
    }

    RecipeCacheStonecutting(CustomRecipeStonecutter recipe) {
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
        return create(new CustomRecipeStonecutter(key));
    }

    @Override
    protected CustomRecipeStonecutter create(CustomRecipeStonecutter recipe) {
        CustomRecipeStonecutter recipeStonecutter = super.create(recipe);
        recipeStonecutter.setSource(source);
        return recipeStonecutter;
    }

    public Ingredient getSource() {
        return source;
    }

    public void setSource(Ingredient source) {
        this.source = source;
    }
}
