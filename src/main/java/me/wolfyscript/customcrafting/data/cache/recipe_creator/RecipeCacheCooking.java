package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import me.wolfyscript.customcrafting.recipes.recipe_item.Ingredient;

public abstract class RecipeCacheCooking<R extends CustomRecipeCooking<R, ?>> extends RecipeCache<R> {

    private Ingredient source;
    private float exp;
    private int cookingTime;

    protected RecipeCacheCooking() {
        super();
    }

    protected RecipeCacheCooking(R recipe) {
        super(recipe);
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
    protected R constructRecipe() {
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
