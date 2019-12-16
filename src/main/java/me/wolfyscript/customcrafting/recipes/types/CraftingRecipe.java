package me.wolfyscript.customcrafting.recipes.types;

import com.sun.javafx.geom.Vec2d;
import me.wolfyscript.customcrafting.recipes.crafting.CraftingData;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface CraftingRecipe<T extends CraftConfig> extends CustomRecipe<T> {

    char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    T getConfig();

    boolean isShapeless();

    CraftingData check(List<List<ItemStack>> matrix);

    default void removeMatrix(List<List<ItemStack>> ingredientsInput, Inventory inventory, int totalAmount, CraftingData craftingData) {
        for (Map.Entry<Vec2d, CustomItem> entry : craftingData.getFoundItems().entrySet()) {
            Vec2d vec = entry.getKey();
            CustomItem item = entry.getValue();
            ItemStack input = ingredientsInput.get((int) vec.y).get((int) vec.x);
            if (item != null) {
                item.consumeItem(input, totalAmount, inventory);
            }
        }
    }

    default int getAmountCraftable(List<List<ItemStack>> matrix, CraftingData craftingData) {
        int totalAmount = -1;
        for (Map.Entry<Vec2d, CustomItem> entry : craftingData.getFoundItems().entrySet()) {
            Vec2d vec = entry.getKey();
            CustomItem item = entry.getValue();
            ItemStack input = matrix.get((int) vec.y).get((int) vec.x);
            if (item != null && input != null) {
                int possible = input.getAmount() / item.getAmount();
                if (possible < totalAmount || totalAmount == -1)
                    totalAmount = possible;
            }
        }
        return totalAmount;
    }

    void load();

    void setResult(List<CustomItem> result);

    void setIngredients(Map<Character, List<CustomItem>> ingredients);

    Map<Character, List<CustomItem>> getIngredients();

    List<CustomItem> getIngredients(int slot);

    CustomItem getIngredient(int slot);
}
