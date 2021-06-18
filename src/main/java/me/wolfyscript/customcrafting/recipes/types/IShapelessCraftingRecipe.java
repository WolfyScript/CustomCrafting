package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IShapelessCraftingRecipe {

    default void checkIngredient(int x, int y, Map<Character, Ingredient> ingredientMap, List<Character> usedKeys, Map<Vec2d, CustomItem> foundItems, Map<Ingredient, Vec2d> mappedIngredients, ItemStack item, boolean exactMatch) {
        if (item == null) return;
        for (Map.Entry<Character, Ingredient> entry : ingredientMap.entrySet()) {
            if (usedKeys.contains(entry.getKey())) continue;
            Optional<CustomItem> validItem = entry.getValue().check(item, exactMatch);
            if (validItem.isPresent()) {
                usedKeys.add(entry.getKey());
                var customItem = validItem.get().clone();
                if (customItem != null) {
                    var vec = new Vec2d(x, y);
                    foundItems.put(vec, customItem);
                    mappedIngredients.put(entry.getValue(), vec);
                }
                return;
            }
        }
    }


}
