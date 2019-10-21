package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbench;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EliteWorkbenchData {

    private int currentGridSize;
    private EliteWorkbench eliteWorkbench;
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

    public EliteWorkbench getEliteWorkbench() {
        return eliteWorkbench;
    }

    public void setEliteWorkbench(EliteWorkbench eliteWorkbench) {
        this.eliteWorkbench = eliteWorkbench;
    }
}
