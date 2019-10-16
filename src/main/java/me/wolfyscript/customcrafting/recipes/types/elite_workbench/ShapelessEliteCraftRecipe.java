package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.types.CraftConfig;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShapelessEliteCraftRecipe extends EliteCraftingRecipe {

    public ShapelessEliteCraftRecipe(CraftConfig config) {
        super(config);
    }

    @Override
    public boolean check(List<List<ItemStack>> matrix) {
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        for (List<ItemStack> items : matrix) {
            for (ItemStack itemStack : items) {
                if (itemStack == null) {
                    continue;
                }
                checkIngredient(allKeys, usedKeys, itemStack);
            }
        }
        return usedKeys.containsAll(getIngredients().keySet());
    }

    private CustomItem checkIngredient(List<Character> allKeys, List<Character> usedKeys, ItemStack item) {
        for (Character key : allKeys) {
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

    @Override
    public List<ItemStack> removeMatrix(List<List<ItemStack>> ingredientsInput, Inventory inventory, ItemStack[] matrix, boolean small, int totalAmount) {
        List<ItemStack> replacements = new ArrayList<>();
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            ItemStack input = matrix[i];
            if (input != null) {
                CustomItem item = checkIngredient(allKeys, usedKeys, input);
                if (item != null) {
                    item.consumeItem(input, totalAmount, inventory);
                }
            }
        }
        return replacements;
    }

    @Override
    public int getAmountCraftable(List<List<ItemStack>> matrix) {
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        int totalAmount = -1;
        for (List<ItemStack> items : matrix) {
            for (ItemStack itemStack : items) {
                if (itemStack != null) {
                    ItemStack result = checkIngredient(allKeys, usedKeys, itemStack);
                    if (result != null) {
                        int possible = itemStack.getAmount() / result.getAmount();
                        if (possible < totalAmount || totalAmount == -1)
                            totalAmount = possible;
                    }
                }
            }
        }
        return totalAmount;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }
}
