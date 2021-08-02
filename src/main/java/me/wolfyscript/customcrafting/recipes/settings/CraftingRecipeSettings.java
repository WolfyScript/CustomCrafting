package me.wolfyscript.customcrafting.recipes.settings;

public interface CraftingRecipeSettings<C extends CraftingRecipeSettings<C>> {

    C clone();
}
