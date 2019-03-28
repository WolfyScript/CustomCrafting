package me.wolfyscript.customcrafting.recipes;


import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public interface CraftingRecipe extends CustomRecipe{

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

    CraftResult removeIngredients(List<List<ItemStack>> matrix, int totalAmount);

    int getAmountCraftable(List<List<ItemStack>> matrix);

    void setPermission(boolean perm);

    void setAdvancedWorkbench(boolean workbench);

    void save();

    void load();

    void setResult(ItemStack result);

    CustomItem getCustomResult();

    void setIngredients(HashMap<Character, List<CustomItem>> ingredients);

    HashMap<Character, List<CustomItem>> getIngredients();

    boolean isSimilar(CraftingRecipe recipe);

    boolean appliesToMatrix(ItemStack[] matrix);



}
