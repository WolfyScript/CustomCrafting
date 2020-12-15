package me.wolfyscript.customcrafting.recipes.types.smithing;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

public class SmithingData {

    private final CustomSmithingRecipe recipe;
    private final CustomItem base, addition;

    public SmithingData(CustomSmithingRecipe recipe, CustomItem base, CustomItem addition) {
        this.recipe = recipe;
        this.base = base;
        this.addition = addition;
    }

    public CustomSmithingRecipe getRecipe() {
        return recipe;
    }

    public CustomItem getBase() {
        return base;
    }

    public CustomItem getAddition() {
        return addition;
    }
}
