package me.wolfyscript.customcrafting.data.cache;

public class ChatLists {

    private int currentPageRecipes;
    private int currentPageItems;

    private String lastUsedRecipe;
    private String lastUsedItem;


    public ChatLists(){
        this.currentPageRecipes = 1;
        this.currentPageItems = 1;
        this.lastUsedRecipe = "";
        this.lastUsedItem = "";
    }

    public int getCurrentPageRecipes() {
        return currentPageRecipes;
    }

    public void setCurrentPageRecipes(int currentPageRecipes) {
        this.currentPageRecipes = currentPageRecipes;
    }

    public int getCurrentPageItems() {
        return currentPageItems;
    }

    public void setCurrentPageItems(int currentPageItems) {
        this.currentPageItems = currentPageItems;
    }

    public String getLastUsedRecipe() {
        return lastUsedRecipe;
    }

    public void setLastUsedRecipe(String lastUsedRecipe) {
        this.lastUsedRecipe = lastUsedRecipe;
    }

    public String getLastUsedItem() {
        return lastUsedItem;
    }

    public void setLastUsedItem(String lastUsedItem) {
        this.lastUsedItem = lastUsedItem;
    }
}
