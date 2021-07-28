package me.wolfyscript.customcrafting.recipes.settings;

public class AdvancedRecipeSettings implements CraftingRecipeSettings {

    private boolean allowVanillaRecipe = true;

    public AdvancedRecipeSettings() {

    }

    public boolean isAllowVanillaRecipe() {
        return allowVanillaRecipe;
    }

    public void setAllowVanillaRecipe(boolean allowVanillaRecipe) {
        this.allowVanillaRecipe = allowVanillaRecipe;
    }
}
