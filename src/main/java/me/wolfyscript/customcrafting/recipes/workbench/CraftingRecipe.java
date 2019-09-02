package me.wolfyscript.customcrafting.recipes.workbench;

import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public interface CraftingRecipe extends CustomRecipe {

    char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    CraftConfig getConfig();

    /*
    Return true if the recipe needs Permission!
     */
    boolean needsPermission();

    /*
    Return true if the recipe can only be crafted in a advanced workbench!
     */
    boolean needsAdvancedWorkbench();

    boolean isShapeless();

    boolean check(List<List<ItemStack>> matrix);

    List<ItemStack> removeMatrix(List<List<ItemStack>> matrix, CraftingInventory inventory, boolean small, int totalAmount);

    int getAmountCraftable(List<List<ItemStack>> matrix);

    void setPermission(boolean perm);

    void setAdvancedWorkbench(boolean workbench);

    void save();

    void load();

    void setResult(List<CustomItem> result);

    CustomItem getCustomResult();

    void setIngredients(HashMap<Character, List<CustomItem>> ingredients);

    HashMap<Character, List<CustomItem>> getIngredients();

    List<CustomItem> getIngredients(int slot);

    CustomItem getIngredient(int slot);
}
