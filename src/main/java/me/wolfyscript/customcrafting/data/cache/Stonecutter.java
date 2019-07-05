package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.Material;

public class Stonecutter {

    private boolean exactMeta;
    private RecipePriority priority;

    private CustomItem source;
    private CustomItem result;

    public Stonecutter(){
        this.source = new CustomItem(Material.AIR);
        this.result = new CustomItem(Material.AIR);
        this.priority = RecipePriority.NORMAL;
        this.exactMeta = true;
    }

    public boolean isExactMeta() {
        return exactMeta;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    public RecipePriority getPriority() {
        return priority;
    }

    public void setPriority(RecipePriority priority) {
        this.priority = priority;
    }

    public CustomItem getSource() {
        return source;
    }

    public void setSource(CustomItem source) {
        this.source = source;
    }

    public CustomItem getResult() {
        return result;
    }

    public void setResult(CustomItem result) {
        this.result = result;
    }
}
