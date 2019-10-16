package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.config.ConfigAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CookingConfig extends RecipeConfig {

    public CookingConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName, String fileType) {
        super(configAPI, folder, type, name, defaultName, fileType);
    }

    public CookingConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName) {
        this(configAPI, folder, type, name, defaultName, CustomCrafting.getConfigHandler().getConfig().getPreferredFileType());
    }

    public CookingConfig(String jsonData, ConfigAPI configAPI, String namespace, String key, String type, String defName) {
        super(jsonData, configAPI, namespace, type, key, defName);
    }

    public void setXP(float xp) {
        set("exp", xp);
    }

    public float getXP() {
        return (float) getDouble("exp");
    }

    public void setCookingTime(int time) {
        set("cooking_time", time);
    }

    public int getCookingTime() {
        return getInt("cooking_time");
    }

    public void setSource(List<CustomItem> source) {
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

    public void setResult(List<CustomItem> results) {
        saveCustomItem("result", results.get(0));
        for (int i = 1; i < results.size(); i++) {
            saveCustomItem("result.variants.var" + i, results.get(i));
        }
    }

    public List<CustomItem> getResult() {
        List<CustomItem> results = new ArrayList<>();
        results.add(getCustomItem("result"));
        if (get("result.variants") != null) {
            Set<String> variants = getValues("result.variants").keySet();
            for (String variant : variants) {
                results.add(getCustomItem("result.variants." + variant));
            }
        }
        return results;
    }

}
