package me.wolfyscript.customcrafting.configs.custom_configs.stonecutter;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.config.ConfigAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StonecutterConfig extends CustomConfig {

    public StonecutterConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        super(configAPI, folder, "stonecutter", name, "stonecutter", fileType);
    }

    public StonecutterConfig(String jsonData, ConfigAPI configAPI, String namespace, String key) {
        super(jsonData, configAPI, namespace, key, "stonecutter", "stonecutter");
    }

    public StonecutterConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, folder, name, "yml");
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

    public void setResult(CustomItem result) {
        saveCustomItem("result", result);
    }

    public List<CustomItem> getResult() {
        List<CustomItem> results = new ArrayList<>();
        results.add(getCustomItem("result"));
        return results;
    }
}
