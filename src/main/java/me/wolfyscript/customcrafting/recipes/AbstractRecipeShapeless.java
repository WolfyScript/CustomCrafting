package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class AbstractRecipeShapeless<C extends AbstractRecipeShapeless<C, S>, S extends CraftingRecipeSettings<S>> extends CraftingRecipe<C, S> {

    protected AbstractRecipeShapeless(NamespacedKey namespacedKey, JsonNode node, int gridSize, Class<S> settingsType) {
        super(namespacedKey, node, gridSize, settingsType);
        constructRecipe();
    }

    protected AbstractRecipeShapeless(int gridSize, S settings) {
        super(gridSize, settings);
    }

    protected AbstractRecipeShapeless(CraftingRecipe<?, S> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public void constructRecipe() {
        this.ingredientsFlat = getIngredients().values().stream().filter(ingredient -> !ingredient.isEmpty()).toList();
        Preconditions.checkArgument(!ingredientsFlat.isEmpty(), "Invalid ingredients! Recipe requires non-air ingredients!");
    }

    @Override
    public boolean fitsDimensions(CraftManager.MatrixData matrixData) {
        return ingredientsFlat.size() == matrixData.getStrippedSize();
    }

    @Override
    public CraftingData check(CraftManager.MatrixData matrixData) {
        List<Integer> usedKeys = new ArrayList<>();
        Map<Integer, IngredientData> dataMap = new HashMap<>();
        ItemStack[] matrix = matrixData.getMatrix();
        for (int i = 0; i < matrix.length; i++) {
            checkIngredient(i, usedKeys, dataMap, matrix[i]);
        }
        return usedKeys.size() == ingredientsFlat.size() ? new CraftingData(this, dataMap) : null;
    }

    protected void checkIngredient(int pos, List<Integer> usedKeys, Map<Integer, IngredientData> dataMap, ItemStack item) {
        if (item == null) return;
        for (int i = 0; i < ingredientsFlat.size(); i++) {
            if (usedKeys.contains(i)) continue;
            Optional<CustomItem> validItem = ingredientsFlat.get(i).check(item, isExactMeta());
            if (validItem.isPresent()) {
                usedKeys.add(i);
                var customItem = validItem.get().clone();
                if (customItem != null) {
                    dataMap.put(pos, new IngredientData(i, ingredientsFlat.get(i), customItem, item));
                }
                return;
            }
        }
    }

    @Override
    public boolean isShapeless() {
        return true;
    }
}
