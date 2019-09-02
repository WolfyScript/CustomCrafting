package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.Material;

import java.util.Collections;
import java.util.List;

public class Stonecutter {

    private boolean exactMeta;
    private RecipePriority priority;

    private List<CustomItem> source;
    private CustomItem result;

    public Stonecutter() {
        this.source = Collections.singletonList(new CustomItem(Material.AIR));
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

    public List<CustomItem> getSource() {
        return source;
    }

    public void setSource(List<CustomItem> source) {
        this.source = source;
    }

    public CustomItem getResult() {
        return result;
    }

    public void setResult(CustomItem result) {
        this.result = result;
    }

    public void setSource(int variant, CustomItem source) {
        List<CustomItem> sources = getSource();
        if (variant < sources.size())
            sources.set(variant, source);
        else
            sources.add(source);
        this.source = sources;
    }
}
