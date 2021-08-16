package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.utils.CraftManager;
import org.bukkit.inventory.Inventory;

public interface ICraftingRecipe {

    String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    boolean isShapeless();

    boolean fitsDimensions(CraftManager.MatrixData matrixData);

    CraftingData check(CraftManager.MatrixData matrixData);

    default void removeMatrix(Inventory inventory, int totalAmount, CraftingData craftingData) {
        craftingData.getIndexedBySlot().forEach((slot, data) -> {
            var item = data.customItem();
            if (item != null) {
                item.remove(data.itemStack(), totalAmount, inventory, null, data.ingredient().isReplaceWithRemains());
            }
        });
    }

    default int getAmountCraftable(CraftingData craftingData) {
        int totalAmount = -1;
        for (IngredientData value : craftingData.getIndexedBySlot().values()) {
            var item = value.customItem();
            if (item != null) {
                var input = value.itemStack();
                if (input != null) {
                    int possible = input.getAmount() / item.getAmount();
                    if (possible < totalAmount || totalAmount == -1) {
                        totalAmount = possible;
                    }
                }
            }
        }
        return totalAmount;
    }
}
