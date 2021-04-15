package me.wolfyscript.customcrafting.recipes.types.smithing;

import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

public class SmithingData {

    private final CustomSmithingRecipe recipe;
    private final Result<?> result;
    private final CustomItem base, addition;

    public SmithingData(CustomSmithingRecipe recipe, Result<?> result, CustomItem base, CustomItem addition) {
        this.recipe = recipe;
        this.result = result;
        this.base = base;
        this.addition = addition;
    }

    public CustomSmithingRecipe getRecipe() {
        return recipe;
    }

    public Result<?> getResult() {
        return result;
    }

    public CustomItem getBase() {
        return base;
    }

    public CustomItem getAddition() {
        return addition;
    }
}
