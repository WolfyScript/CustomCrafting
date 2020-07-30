package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.api.utils.NamespacedKey;

public class ChatLists {

    private int currentPageRecipes;
    private int currentPageItems;

    private String lastUsedRecipe;
    private NamespacedKey lastUsedItem;


    public ChatLists() {
        this.currentPageRecipes = 1;
        this.currentPageItems = 1;
        this.lastUsedRecipe = "";
        this.lastUsedItem = null;
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

    public NamespacedKey getLastUsedItem() {
        return lastUsedItem;
    }

    public void setLastUsedItem(NamespacedKey lastUsedItem) {
        this.lastUsedItem = lastUsedItem;
    }
}
