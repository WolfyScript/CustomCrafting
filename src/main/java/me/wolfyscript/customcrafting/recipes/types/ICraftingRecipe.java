package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface ICraftingRecipe {

    String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    boolean isShapeless();

    CraftingData check(List<ItemStack> flatMatrix);

    void constructRecipe();

    default void removeMatrix(List<ItemStack> matrix, Inventory inventory, int totalAmount, CraftingData craftingData) {
        craftingData.getIndexedBySlot().forEach((slot, data) -> {
            if (matrix.size() > slot) {
                var item = data.customItem();
                if (item != null) {
                    item.remove(matrix.get(slot), totalAmount, inventory, null, data.ingredient().isReplaceWithRemains());
                }
            }
        });
    }

    default int getAmountCraftable(List<ItemStack> matrix, CraftingData craftingData) {
        int totalAmount = -1;
        for (Map.Entry<Integer, IngredientData> entry : craftingData.getIndexedBySlot().entrySet()) {
            if (matrix.size() > entry.getKey()) {
                var item = entry.getValue().customItem();
                if (item != null) {
                    ItemStack input = matrix.get(entry.getKey());
                    if (input != null) {
                        int possible = input.getAmount() / item.getAmount();
                        if (possible < totalAmount || totalAmount == -1) {
                            totalAmount = possible;
                        }
                    }
                }
            }
        }
        return totalAmount;
    }

    @Nullable
    default Ingredient getIngredients(char key) {
        return getIngredients().get(key);
    }

    void setIngredient(char key, Ingredient ingredients);

    Map<Character, Ingredient> getIngredients();

    void setIngredients(Map<Character, Ingredient> ingredients);
}
