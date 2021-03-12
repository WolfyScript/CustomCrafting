package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.utils.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IShapelessCraftingRecipe {

    default CustomItem checkIngredient(Map<Character, Ingredient> ingredientMap,  List<Character> usedKeys, ItemStack item, boolean exactMatch) {
        for (Character key : ingredientMap.keySet()) {
            if (usedKeys.contains(key)) continue;
            Optional<CustomItem> validItem = ingredientMap.get(key).check(item, exactMatch);
            if(validItem.isPresent()) {
                usedKeys.add(key);
                return validItem.get().clone();
            }
        }
        return null;
    }


}
