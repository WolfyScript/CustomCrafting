package me.wolfyscript.customcrafting.recipes.types.grindstone;

import me.wolfyscript.utilities.api.custom_items.CustomItem;

public class GrindstoneData {

    private GrindstoneRecipe recipe;
    private CustomItem inputTop, inputBottom;

    public GrindstoneData(GrindstoneRecipe recipe, CustomItem inputTop, CustomItem inputBottom) {
        this.recipe = recipe;
        this.inputTop = inputTop;
        this.inputBottom = inputBottom;
    }

    public CustomItem getInputBottom() {
        return inputBottom;
    }

    public CustomItem getInputTop() {
        return inputTop;
    }

    public GrindstoneRecipe getRecipe() {
        return recipe;
    }
}
