package me.wolfyscript.customcrafting.recipes.types.grindstone;

import me.wolfyscript.utilities.api.custom_items.CustomItem;

public class GrindstoneData {

    private final GrindstoneRecipe recipe;
    private final CustomItem inputTop;
    private final CustomItem inputBottom;
    private final boolean validItem;

    public GrindstoneData(GrindstoneRecipe recipe, boolean validItem, CustomItem inputTop, CustomItem inputBottom) {
        this.recipe = recipe;
        this.validItem = validItem;
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

    public boolean isValidItem() {
        return validItem;
    }
}
