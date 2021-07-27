package me.wolfyscript.customcrafting.recipes.data;

import me.wolfyscript.customcrafting.recipes.types.CustomSmithingRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

import java.util.Map;

public class SmithingData extends RecipeData<CustomSmithingRecipe> {

    public SmithingData(CustomSmithingRecipe recipe, Map<Integer, IngredientData> ingredients) {
        super(recipe, ingredients);
    }

    public CustomItem getBase() {
        return getBySlot(0).customItem();
    }

    public CustomItem getAddition() {
        return getBySlot(1).customItem();
    }
}
