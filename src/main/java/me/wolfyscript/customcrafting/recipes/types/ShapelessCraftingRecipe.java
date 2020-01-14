package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.crafting.CraftingData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface ShapelessCraftingRecipe<T extends CraftConfig> extends CraftingRecipe<T> {

    @Override
    default CraftingData check(List<List<ItemStack>> matrix) {
        List<Character> usedKeys = new ArrayList<>();
        HashMap<Vec2d, CustomItem> foundItems = new HashMap<>();
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                ItemStack itemStack = matrix.get(i).get(j);
                if (itemStack == null) {
                    continue;
                }
                CustomItem item = checkIngredient(usedKeys, itemStack);
                if (item != null) {
                    foundItems.put(new Vec2d(j, i), item);
                }
            }
        }
        if (usedKeys.containsAll(getIngredients().keySet())) {
            return new CraftingData((CraftingRecipe<CraftConfig>) this, foundItems);
        }
        return null;
    }

    default CustomItem checkIngredient(List<Character> usedKeys, ItemStack item) {
        for (Character key : getIngredients().keySet()) {
            if (!usedKeys.contains(key)) {
                for (CustomItem ingredient : getIngredients().get(key)) {
                    if (!ingredient.isSimilar(item, isExactMeta())) {
                        continue;
                    }
                    usedKeys.add(key);
                    return ingredient.clone();
                }
            }
        }
        return null;
    }

}
