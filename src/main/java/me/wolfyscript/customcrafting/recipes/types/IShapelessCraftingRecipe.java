package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public interface IShapelessCraftingRecipe {

    default CraftingData check(CraftingRecipe<?> recipe, Map<Character, Ingredient> recipeIngredients, boolean exactMeta, List<List<ItemStack>> ingredients) {
        List<Character> usedKeys = new ArrayList<>();
        Map<Vec2d, IngredientData> dataMap = new HashMap<>();
        for (int i = 0; i < ingredients.size(); i++) {
            for (int j = 0; j < ingredients.get(i).size(); j++) {
                checkIngredient(j, i, recipeIngredients, usedKeys, dataMap, ingredients.get(i).get(j), exactMeta);
            }
        }
        return usedKeys.containsAll(recipeIngredients.keySet()) ? new CraftingData(recipe, dataMap) : null;
    }

    default void checkIngredient(int x, int y, Map<Character, Ingredient> ingredientMap, List<Character> usedKeys, Map<Vec2d, IngredientData> dataMap, ItemStack item, boolean exactMatch) {
        if (item == null) return;
        for (Map.Entry<Character, Ingredient> entry : ingredientMap.entrySet()) {
            if (usedKeys.contains(entry.getKey())) continue;
            Optional<CustomItem> validItem = entry.getValue().check(item, exactMatch);
            if (validItem.isPresent()) {
                usedKeys.add(entry.getKey());
                var customItem = validItem.get().clone();
                if (customItem != null) {
                    dataMap.put(new Vec2d(x, y), new IngredientData(CraftingRecipe.LETTERS.indexOf(entry.getKey()), entry.getValue(), customItem, item));
                }
                return;
            }
        }
    }


}
