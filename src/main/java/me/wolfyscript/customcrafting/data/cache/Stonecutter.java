package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stonecutter extends RecipeData {

    private List<CustomItem> source;
    private CustomItem result;

    public Stonecutter() {
        super();
        this.source = new ArrayList<>(Collections.singletonList(new CustomItem(Material.AIR)));
        this.result = new CustomItem(Material.AIR);
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
