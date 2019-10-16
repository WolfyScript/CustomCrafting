package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public interface CraftingRecipe extends CustomRecipe {

    char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    CraftConfig getConfig();

    boolean isShapeless();

    boolean check(List<List<ItemStack>> matrix);

    List<ItemStack> removeMatrix(List<List<ItemStack>> ingredientsInput, Inventory inventory, ItemStack[] matrix, boolean small, int totalAmount);

    int getAmountCraftable(List<List<ItemStack>> matrix);

    void save();

    void load();

    void setResult(List<CustomItem> result);

    void setIngredients(HashMap<Character, List<CustomItem>> ingredients);

    HashMap<Character, List<CustomItem>> getIngredients();

    List<CustomItem> getIngredients(int slot);

    CustomItem getIngredient(int slot);
}
