package me.wolfyscript.customcrafting.data.cache;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EliteWorkbenchData {

    private int currentGridSize;
    private ItemStack result;
    private ItemStack[] contents;

    public EliteWorkbenchData(){
        this.contents = null;
        this.currentGridSize = 3;
        this.result = new ItemStack(Material.AIR);
    }

    public ItemStack[] getContents(){
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public int getCurrentGridSize() {
        return currentGridSize;
    }

    public void setCurrentGridSize(int currentGridSize) {
        this.currentGridSize = currentGridSize;
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }
}
