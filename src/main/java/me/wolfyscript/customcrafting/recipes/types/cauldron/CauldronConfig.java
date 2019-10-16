package me.wolfyscript.customcrafting.recipes.types.cauldron;

import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.config.ConfigAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CauldronConfig extends RecipeConfig {

    public CauldronConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, folder, "cauldron", name, "cauldron");
    }

    public CauldronConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        super(configAPI, folder, "cauldron", name, "cauldron", fileType);
    }

    public CauldronConfig(String jsonData, ConfigAPI configAPI, String namespace, String key) {
        super(jsonData, configAPI, namespace, key, "cauldron", "cauldron");
    }

    public boolean dropItems(){
        return getBoolean("dropItems");
    }

    public void setDropItems(boolean dropItems){
        set("dropItems", dropItems);
    }

    public void setXP(float xp) {
        set("exp", xp);
    }

    public float getXP() {
        return (float) getDouble("exp");
    }

    public void setCookingTime(int cookingTime){
        set("cookingTime", cookingTime);
    }

    public int getCookingTime(){
        return getInt("cookingTime");
    }

    public void setIngredients(List<CustomItem> source) {
        for (int i = 0; i < source.size(); i++) {
            saveCustomItem("ingredients.var" + i, source.get(i));
        }
    }

    public List<CustomItem> getIngredients() {
        List<CustomItem> sources = new ArrayList<>();
        if (get("ingredients") != null) {
            Set<String> variants = getValues("ingredients").keySet();
            for (String variant : variants) {
                sources.add(getCustomItem("ingredients." + variant));
            }
        }
        return sources;
    }

    public void setResult(CustomItem customItem){
        saveCustomItem("result", customItem);
    }

    public CustomItem getResult(){
        return getCustomItem("result");
    }
}
