package me.wolfyscript.customcrafting.recipes.data;


import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AnvilData {

    private final CustomRecipeAnvil recipe;
    private final CustomItem inputLeft;
    private final CustomItem inputRight;
    private final Optional<Result<?>> result;

    public AnvilData(CustomRecipeAnvil recipe, @Nullable Result<?> result, CustomItem inputLeft, CustomItem inputRight) {
        this.recipe = recipe;
        this.inputLeft = inputLeft;
        this.inputRight = inputRight;
        this.result = Optional.ofNullable(result);
    }

    public CustomRecipeAnvil getRecipe() {
        return recipe;
    }

    public CustomItem getInputLeft() {
        return inputLeft;
    }

    public CustomItem getInputRight() {
        return inputRight;
    }

    public Optional<Result<?>> getResult() {
        return result;
    }
}
