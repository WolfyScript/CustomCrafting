package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class AbstractShapelessCraftingRecipe<C extends AbstractShapelessCraftingRecipe<C>> extends CraftingRecipe<C> {

    protected AbstractShapelessCraftingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    protected AbstractShapelessCraftingRecipe() {
        super();
    }

    protected AbstractShapelessCraftingRecipe(CraftingRecipe<?> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public CraftingData check(List<List<ItemStack>> matrix) {
        List<Character> usedKeys = new ArrayList<>();
        Map<Vec2d, IngredientData> dataMap = new HashMap<>();
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                checkIngredient(j, i, usedKeys, dataMap, matrix.get(i).get(j));
            }
        }
        return usedKeys.containsAll(getIngredients().keySet()) ? new CraftingData(this, dataMap) : null;
    }

    protected void checkIngredient(int x, int y, List<Character> usedKeys, Map<Vec2d, IngredientData> dataMap, ItemStack item) {
        if (item == null) return;
        for (Map.Entry<Character, Ingredient> entry : getIngredients().entrySet()) {
            if (usedKeys.contains(entry.getKey())) continue;
            Optional<CustomItem> validItem = entry.getValue().check(item, isExactMeta());
            if (validItem.isPresent()) {
                usedKeys.add(entry.getKey());
                var customItem = validItem.get().clone();
                if (customItem != null) {
                    dataMap.put(new Vec2d(x, y), new IngredientData(ICraftingRecipe.LETTERS.indexOf(entry.getKey()), entry.getValue(), customItem, item));
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
