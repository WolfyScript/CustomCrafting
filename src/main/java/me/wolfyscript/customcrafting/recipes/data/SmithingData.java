package me.wolfyscript.customcrafting.recipes.data;

import me.wolfyscript.customcrafting.recipes.CustomRecipeSmithing;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

import java.util.Map;

public class SmithingData extends RecipeData<CustomRecipeSmithing> {

    public SmithingData(CustomRecipeSmithing recipe, Map<Integer, IngredientData> ingredients) {
        super(recipe, ingredients);
    }

    public CustomItem getBase() {
        return getBySlot(0).customItem();
    }

    public CustomItem getAddition() {
        return getBySlot(1).customItem();
    }
}
