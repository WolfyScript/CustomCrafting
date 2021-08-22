package me.wolfyscript.customcrafting.recipes.settings;

public class EliteRecipeSettings implements CraftingRecipeSettings<EliteRecipeSettings> {

    public EliteRecipeSettings() {

    }

    @Override
    public EliteRecipeSettings clone() {
        return new EliteRecipeSettings();
    }
}
