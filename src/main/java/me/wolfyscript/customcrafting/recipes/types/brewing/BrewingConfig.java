package me.wolfyscript.customcrafting.recipes.types.brewing;

import com.google.gson.JsonObject;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BrewingConfig extends RecipeConfig {

    public BrewingConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName, String fileType) {
        super(configAPI, folder, type, name, defaultName, fileType);
    }

    public BrewingConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        super(configAPI, folder, "brewing", name, "brewing", fileType);
    }

    public BrewingConfig(String jsonData, ConfigAPI configAPI, String namespace, String key) {
        super(jsonData, configAPI, namespace, key, "brewing", "brewing");
    }

    public BrewingConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, folder, name, "json");
    }

    public BrewingConfig() {
        super("brewing");
    }


    public List<CustomItem> getIngredient() {
        List<CustomItem> sources = new ArrayList<>();
        sources.add(getCustomItem("ingredient"));
        if (get("ingredient.variants") != null) {
            Set<String> variants = getValues("ingredient.variants").keySet();
            for (String variant : variants) {
                sources.add(getCustomItem("ingredient.variants." + variant));
            }
        }
        return sources;
    }

    public void setIngredient(List<CustomItem> ingredient) {
        set("ingredient", new JsonObject());
        saveCustomItem("ingredient", ingredient.get(0));
        for (int i = 1; i < ingredient.size(); i++) {
            saveCustomItem("ingredient.variants.var" + i, ingredient.get(i));
        }
    }

    public List<CustomItem> getAllowedItems() {
        List<CustomItem> sources = new ArrayList<>();
        sources.add(getCustomItem("allowed_items"));
        if (get("allowed_items.variants") != null) {
            Set<String> variants = getValues("allowed_items.variants").keySet();
            for (String variant : variants) {
                sources.add(getCustomItem("allowed_items.variants." + variant));
            }
        }
        return sources;
    }

    public void setAllowedItems(List<CustomItem> items) {
        set("allowed_items", new JsonObject());
        saveCustomItem("allowed_items", items.get(0));
        for (int i = 1; i < items.size(); i++) {
            saveCustomItem("allowed_items.variants.var" + i, items.get(i));
        }
    }

    public int getBrewTime() {
        return getInt("brew_time");
    }

    public void setBrewTime(int fuelCost) {
        set("brew_time", fuelCost);
    }

    public int getFuelCost() {
        return getInt("fuel_cost");
    }

    public void setFuelCost(int fuelCost) {
        set("fuel_cost", fuelCost);
    }


}
