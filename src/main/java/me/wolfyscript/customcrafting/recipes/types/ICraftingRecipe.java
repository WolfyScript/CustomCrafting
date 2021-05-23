package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface ICraftingRecipe {

    boolean isShapeless();

    CraftingData check(ItemStack[] matrix, List<List<ItemStack>> ingredients);

    default void removeMatrix(List<List<ItemStack>> matrix, Inventory inventory, int totalAmount, CraftingData craftingData) {
        for (Map.Entry<Vec2d, CustomItem> entry : craftingData.getFoundItems().entrySet()) {
            Vec2d vec = entry.getKey();
            CustomItem item = entry.getValue();
            if (matrix.size() > vec.y && matrix.get((int) vec.y).size() > vec.x) {
                ItemStack input = matrix.get((int) vec.y).get((int) vec.x);
                if (item != null) {
                    item.consumeItem(input, totalAmount, inventory);
                }
            }
        }
    }

    default int getAmountCraftable(List<List<ItemStack>> matrix, CraftingData craftingData) {
        int totalAmount = -1;
        for (Map.Entry<Vec2d, CustomItem> entry : craftingData.getFoundItems().entrySet()) {
            Vec2d vec = entry.getKey();
            CustomItem item = entry.getValue();
            if (matrix.size() > vec.y && matrix.get((int) vec.y).size() > vec.x) {
                ItemStack input = matrix.get((int) vec.y).get((int) vec.x);
                if (item != null && input != null) {
                    int possible = input.getAmount() / item.getAmount();
                    if (possible < totalAmount || totalAmount == -1)
                        totalAmount = possible;
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
