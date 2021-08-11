package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;

public class RecipeCacheCooking extends RecipeCache<CustomRecipeCooking<?, ?>> {

    private Ingredient source;
    private float exp;
    private int cookingTime;

    public RecipeCacheCooking(RecipeCreatorCache creatorCache) {
        super(creatorCache);
    }

    public RecipeCacheCooking(RecipeCreatorCache creatorCache, CustomRecipeCooking<?, ?> recipe) {
        super(creatorCache, recipe);
        this.source = recipe.getSource();
        this.exp = recipe.getExp();
        this.cookingTime = recipe.getCookingTime();
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
    protected CustomRecipeCooking<?, ?> constructRecipe() {
        return null;
    }

    public Ingredient getSource() {
        return source;
    }

    public void setSource(Ingredient source) {
        this.source = source;
    }

    public float getExp() {
        return exp;
    }

    public void setExp(float exp) {
        this.exp = exp;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }
}
