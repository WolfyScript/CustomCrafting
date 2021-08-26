package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;

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

    public Ingredient getSource() {
        return source;
    }

    public void setSource(Ingredient source) {
        this.source = source;
    }

    @Override
    protected R create(R recipe) {
        R cookingRecipe = super.create(recipe);
        cookingRecipe.setCookingTime(cookingTime);
        cookingRecipe.setSource(source);
        cookingRecipe.setExp(exp);
        return cookingRecipe;
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
