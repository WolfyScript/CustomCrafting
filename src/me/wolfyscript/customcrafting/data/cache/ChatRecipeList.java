package me.wolfyscript.customcrafting.data.cache;

public class ChatRecipeList {

    private int currentPage;

    private String previousRecipe;


    public ChatRecipeList(){
        this.currentPage = 1;
        this.previousRecipe = "";
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getPreviousRecipe() {
        return previousRecipe;
    }

    public void setPreviousRecipe(String previousRecipe) {
        this.previousRecipe = previousRecipe;
    }
}
