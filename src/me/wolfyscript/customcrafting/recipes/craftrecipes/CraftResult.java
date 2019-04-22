package me.wolfyscript.customcrafting.recipes.craftrecipes;

import org.bukkit.inventory.ItemStack;

public class CraftResult {

    private ItemStack[] matrix;
    private int amount;

    public CraftResult(ItemStack[] matrix, int amount){
        this.matrix = matrix;
        this.amount = amount;
    }


    public ItemStack[] getMatrix() {
        return matrix;
    }

    public int getAmount() {
        return amount;
    }
}
