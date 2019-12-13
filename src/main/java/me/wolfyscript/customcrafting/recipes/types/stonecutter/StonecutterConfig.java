package me.wolfyscript.customcrafting.recipes.types.stonecutter;

import com.google.gson.JsonObject;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StonecutterConfig extends RecipeConfig {

    public StonecutterConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        super(configAPI, folder, "stonecutter", name, "stonecutter", fileType);
    }

    public StonecutterConfig(String jsonData, ConfigAPI configAPI, String namespace, String key) {
        super(jsonData, configAPI, namespace, key, "stonecutter", "stonecutter");
    }

    public StonecutterConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, folder, name, "json");
    }

    public StonecutterConfig() {
        super("stonecutter");
    }

    public void setSource(List<CustomItem> source) {
        set("source", new JsonObject());
        saveCustomItem("source", source.get(0));
        for (int i = 1; i < source.size(); i++) {
            saveCustomItem("source.variants.var" + i, source.get(i));
        }
    }

    public List<CustomItem> getSource() {
        List<CustomItem> sources = new ArrayList<>();
        sources.add(getCustomItem("source"));
        if (get("source.variants") != null) {
            Set<String> variants = getValues("source.variants").keySet();
            for (String variant : variants) {
                sources.add(getCustomItem("source.variants." + variant));
            }
        }
        return sources;
    }

    public void setResult(CustomItem result) {
        saveCustomItem("result", result);
    }

    public void setSource(int variant, CustomItem source) {
        List<CustomItem> sources = getSource();
        if (variant < sources.size())
            sources.set(variant, source);
        else
            sources.add(source);
        setSource(sources);
    }
}
