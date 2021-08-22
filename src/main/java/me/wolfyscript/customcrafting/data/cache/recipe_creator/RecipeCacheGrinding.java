package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeGrindstone;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;

public class RecipeCacheGrinding extends RecipeCache<CustomRecipeGrindstone> {

    private Ingredient inputTop;
    private Ingredient inputBottom;
    private int xp;

    RecipeCacheGrinding() {
        super();
    }

    RecipeCacheGrinding(CustomRecipeGrindstone recipe) {
        super(recipe);
        this.inputTop = recipe.getInputTop().clone();
        this.inputBottom = recipe.getInputBottom().clone();
        this.xp = recipe.getXp();
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        if (slot == 0) {
            setInputTop(ingredient);
        } else {
            setInputBottom(ingredient);
        }
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? getInputTop() : getInputBottom();
    }

    @Override
    protected CustomRecipeGrindstone constructRecipe() {
        return null;
    }

    public Ingredient getInputTop() {
        return inputTop;
    }

    public void setInputTop(Ingredient inputTop) {
        this.inputTop = inputTop;
    }

    public Ingredient getInputBottom() {
        return inputBottom;
    }

    public void setInputBottom(Ingredient inputBottom) {
        this.inputBottom = inputBottom;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
