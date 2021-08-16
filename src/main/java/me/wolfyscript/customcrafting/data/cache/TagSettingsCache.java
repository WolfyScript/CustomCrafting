package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.recipes.recipe_item.RecipeItemStack;

public class TagSettingsCache {

    private int listPage;
    private int chooseListPage;
    private RecipeItemStack recipeItemStack;

    public TagSettingsCache() {
        this.chooseListPage = 0;
        this.listPage = 0;
        this.recipeItemStack = null;
    }

    public int getListPage() {
        return listPage;
    }

    public void setListPage(int listPage) {
        this.listPage = listPage;
    }

    public int getChooseListPage() {
        return chooseListPage;
    }

    public void setChooseListPage(int chooseListPage) {
        this.chooseListPage = chooseListPage;
    }

    public RecipeItemStack getRecipeItemStack() {
        return recipeItemStack;
    }

    public void setRecipeItemStack(RecipeItemStack recipeItemStack) {
        this.recipeItemStack = recipeItemStack;
    }
}
