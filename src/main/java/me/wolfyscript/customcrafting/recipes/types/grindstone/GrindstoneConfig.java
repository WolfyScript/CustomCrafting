package me.wolfyscript.customcrafting.recipes.types.grindstone;

import com.google.gson.JsonObject;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GrindstoneConfig extends RecipeConfig {

    public GrindstoneConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, folder, "grindstone", name, "grindstone");
    }

    public GrindstoneConfig(String jsonData, ConfigAPI configAPI, String namespace, String key) {
        super(jsonData, configAPI, namespace, key, "grindstone", "grindstone");
    }

    public GrindstoneConfig() {
        super("grindstone");
    }

    public List<CustomItem> getInputTop() {
        List<CustomItem> sources = new ArrayList<>();
        sources.add(getCustomItem("input_top"));
        if (get("input_top.variants") != null) {
            Set<String> variants = getValues("input_top.variants").keySet();
            for (String variant : variants) {
                sources.add(getCustomItem("input_top.variants." + variant));
            }
        }
        return sources;
    }

    public void setInputTop(List<CustomItem> input) {
        set("input_top", new JsonObject());
        saveCustomItem("input_top", input.get(0));
        for (int i = 1; i < input.size(); i++) {
            saveCustomItem("input_top.variants.var" + i, input.get(i));
        }
    }

    public void setInputTop(int variant, CustomItem input) {
        List<CustomItem> inputs = getInputTop();
        if (variant < inputs.size())
            inputs.set(variant, input);
        else
            inputs.add(input);
        setInputTop(inputs);
    }

    public List<CustomItem> getInputBottom() {
        List<CustomItem> sources = new ArrayList<>();
        sources.add(getCustomItem("input_bottom"));
        if (get("input_bottom.variants") != null) {
            Set<String> variants = getValues("input_bottom.variants").keySet();
            for (String variant : variants) {
                sources.add(getCustomItem("input_bottom.variants." + variant));
            }
        }
        return sources;
    }

    public void setInputBottom(List<CustomItem> input) {
        set("input_bottom", new JsonObject());
        saveCustomItem("input_bottom", input.get(0));
        for (int i = 1; i < input.size(); i++) {
            saveCustomItem("input_bottom.variants.var" + i, input.get(i));
        }
    }

    public void setInputBottom(int variant, CustomItem input) {
        List<CustomItem> inputs = getInputBottom();
        if (variant < inputs.size())
            inputs.set(variant, input);
        else
            inputs.add(input);
        setInputBottom(inputs);
    }

    public float getXP() {
        return (float) getDouble("exp");
    }

    public void setXP(float xp) {
        set("exp", xp);
    }
}
