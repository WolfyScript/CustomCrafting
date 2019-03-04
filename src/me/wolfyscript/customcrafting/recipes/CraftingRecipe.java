package me.wolfyscript.customcrafting.recipes;


import me.wolfyscript.customcrafting.items.CustomItem;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public interface CraftingRecipe extends CustomRecipe{

    /*
    Return true if the recipe needs Permission!
     */
    boolean needsPermission();

    /*
    Return true if the recipe can only be crafted in a advanced workbench!
     */
    boolean needsAdvancedWorkbench();

    boolean check(ItemStack[] matrix);

    void setPermission(boolean perm);

    void setAdvancedWorkbench(boolean workbench);

    void save();

    void load();

    void setResult(ItemStack result);

    CustomItem getResult();

    void setIngredients(HashMap<Character, HashMap<ItemStack, List<String>>> ingredients);

    HashMap<Character, HashMap<ItemStack, List<String>>> getIngredients();




}
