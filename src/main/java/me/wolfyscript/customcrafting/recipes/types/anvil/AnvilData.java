package me.wolfyscript.customcrafting.recipes.types.anvil;

import me.wolfyscript.utilities.api.custom_items.CustomItem;

public class AnvilData {

    private CustomAnvilRecipe recipe;
    private CustomItem inputLeft, inputRight;

    public AnvilData(CustomAnvilRecipe recipe, CustomItem inputLeft, CustomItem inputRight) {
        this.recipe = recipe;
        this.inputLeft = inputLeft;
        this.inputRight = inputRight;
    }

    public CustomAnvilRecipe getRecipe() {
        return recipe;
    }

    public CustomItem getInputLeft() {
        return inputLeft;
    }

    public CustomItem getInputRight() {
        return inputRight;
    }
}
