package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface CraftingRecipe<T extends CraftConfig> extends CustomRecipe<T> {

    char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    T getConfig();

    boolean isShapeless();

    boolean check(List<List<ItemStack>> matrix);

    List<ItemStack> removeMatrix(List<List<ItemStack>> ingredientsInput, Inventory inventory, ItemStack[] matrix, boolean small, int totalAmount);

    int getAmountCraftable(List<List<ItemStack>> matrix);

    void load();

    void setResult(List<CustomItem> result);

    void setIngredients(Map<Character, List<CustomItem>> ingredients);

    Map<Character, List<CustomItem>> getIngredients();

    List<CustomItem> getIngredients(int slot);

    CustomItem getIngredient(int slot);
}
