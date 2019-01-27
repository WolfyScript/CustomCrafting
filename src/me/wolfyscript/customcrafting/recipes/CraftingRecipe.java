package me.wolfyscript.customcrafting.recipes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public interface CraftingRecipe extends Recipe {
    boolean needsPermission();
    boolean needsAdvancedWorkbench();
    String getID();
    String getGroup();
    boolean check(ItemStack[] matrix);
}
