package me.wolfyscript.customcrafting.recipes.types;

import com.google.gson.JsonObject;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CookingConfig extends RecipeConfig {

    public CookingConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName, String fileType) {
        super(configAPI, folder, type, name, defaultName, fileType);
    }

    public CookingConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName) {
        this(configAPI, folder, type, name, defaultName, "json");
    }

    public CookingConfig(String jsonData, ConfigAPI configAPI, String namespace, String key, String type, String defName) {
        super(jsonData, configAPI, namespace, type, key, defName);
    }

    public CookingConfig(String type, String defaultName) {
        super(type, defaultName);
    }

    public CookingConfig(String type) {
        super(type);
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

    @Override
    public List<CustomItem> getResult() {
        return super.getResult();
    }

    @Override
    public void setResult(List<CustomItem> results) {
        super.setResult(results);
    }

    public void setIngredients(int slot, List<CustomItem> ingredient) {
        if (slot == 0) {
            setSource(ingredient);
        } else {
            setResult(ingredient);
        }
    }

    public List<CustomItem> getIngredients(int slot) {
        if (slot == 0) {
            return getSource();
        }
        return getResult();
    }

    public void setIngredient(int slot, int variant, CustomItem ingredient) {
        List<CustomItem> ingredients = getIngredients(slot);
        if (variant < ingredients.size())
            ingredients.set(variant, ingredient);
        else
            ingredients.add(ingredient);
        setIngredients(slot, ingredients);
    }

}
