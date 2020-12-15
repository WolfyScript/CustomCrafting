package me.wolfyscript.customcrafting.recipes.types.anvil;


import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

public class AnvilData {

    private final CustomAnvilRecipe recipe;
    private final CustomItem inputLeft;
    private final CustomItem inputRight;

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
