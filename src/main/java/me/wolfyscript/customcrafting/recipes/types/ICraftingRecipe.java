package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ICraftingRecipe {

    String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    boolean isShapeless();

    CraftingData check(CraftManager.MatrixData flatMatrix);

    void constructRecipe();

    default void removeMatrix(CraftManager.MatrixData matrix, Inventory inventory, int totalAmount, CraftingData craftingData) {
        craftingData.getIndexedBySlot().forEach((slot, data) -> {
            if (matrix.getMatrix().length > slot) {
                var item = data.customItem();
                if (item != null) {
                    item.remove(matrix.getMatrix()[slot], totalAmount, inventory, null, data.ingredient().isReplaceWithRemains());
                }
            }
        });
    }

    default int getAmountCraftable(CraftManager.MatrixData matrix, CraftingData craftingData) {
        int totalAmount = -1;
        for (Map.Entry<Integer, IngredientData> entry : craftingData.getIndexedBySlot().entrySet()) {
            if (matrix.getMatrix().length > entry.getKey()) {
                var item = entry.getValue().customItem();
                if (item != null) {
                    ItemStack input = matrix.getMatrix()[entry.getKey()];
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
