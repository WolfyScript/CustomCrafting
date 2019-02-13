package me.wolfyscript.customcrafting.recipes;


import org.bukkit.inventory.ItemStack;

public interface CraftingRecipe extends CustomRecipe{

    boolean needsPermission();
    boolean needsAdvancedWorkbench();
    boolean check(ItemStack[] matrix);

}
