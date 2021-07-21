package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface ICraftingRecipe {

    String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    boolean isShapeless();

    CraftingData check(List<List<ItemStack>> matrix);

    default void removeMatrix(List<List<ItemStack>> matrix, Inventory inventory, int totalAmount, CraftingData craftingData) {
        for (Map.Entry<Vec2d, IngredientData> entry : craftingData.getIngredients().entrySet()) {
            Vec2d vec = entry.getKey();
            if (matrix.size() > vec.y && matrix.get((int) vec.y).size() > vec.x) {
                ItemStack input = matrix.get((int) vec.y).get((int) vec.x);
                var item = entry.getValue().customItem();
                if (item != null) {
                    item.remove(input, totalAmount, inventory, null, entry.getValue().ingredient().isReplaceWithRemains());
                }
            }
        }
    }

    default int getAmountCraftable(List<List<ItemStack>> matrix, CraftingData craftingData) {
        int totalAmount = -1;
        for (Map.Entry<Vec2d, IngredientData> entry : craftingData.getIngredients().entrySet()) {
            Vec2d vec = entry.getKey();
            if (matrix.size() > vec.y && matrix.get((int) vec.y).size() > vec.x) {
                ItemStack input = matrix.get((int) vec.y).get((int) vec.x);
                var item = entry.getValue().customItem();
                if (item != null && input != null) {
                    int possible = input.getAmount() / item.getAmount();
                    if (possible < totalAmount || totalAmount == -1) {
                        totalAmount = possible;
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
