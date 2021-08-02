package me.wolfyscript.customcrafting.recipes.data;


import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

import java.util.Map;

public class AnvilData extends RecipeData<CustomRecipeAnvil> {

    private boolean usedResult;

    public AnvilData(CustomRecipeAnvil recipe, Map<Integer, IngredientData> dataMap) {
        super(recipe, dataMap);
        this.usedResult = false;
    }

    @Deprecated
    public CustomItem getInputLeft() {
        return getBySlot(0).customItem();
    }

    @Deprecated
    public CustomItem getInputRight() {
        return getBySlot(1).customItem();
    }

    public IngredientData getLeftIngredient() {
        return getBySlot(0);
    }

    public IngredientData getRightIngredient() {
        return getBySlot(1);
    }

    public boolean isUsedResult() {
        return usedResult;
    }

    public void setUsedResult(boolean usedResult) {
        this.usedResult = usedResult;
    }
}
