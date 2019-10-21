package me.wolfyscript.customcrafting.recipes.types.cauldron;

import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Material;

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

    public void setWaterLevel(int waterLevel){
        set("waterLevel", waterLevel);
    }

    public int getWaterLevel(){
        return getInt("waterLevel");
    }

    public void setNoWater(boolean noWater){
        set("noWater",noWater);
    }

    public boolean isNoWater(){
        return getBoolean("noWater");
    }

    public boolean needsFire(){
        return getBoolean("fire");
    }

    public void setFire(boolean needsFire){
        set("fire", needsFire);
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

    public void setResult(List<CustomItem> results) {
        saveCustomItem("result", results.get(0));
        for (int i = 1; i < results.size(); i++) {
            if(!results.get(i).getType().equals(Material.AIR)){
                saveCustomItem("result.variants.var" + i, results.get(i));
            }
        }
    }

    public List<CustomItem> getResult() {
        List<CustomItem> results = new ArrayList<>();
        results.add(getCustomItem("result"));
        if (get("result.variants") != null) {
            Set<String> variants = getValues("result.variants").keySet();
            for (String variant : variants) {
                CustomItem customItem = getCustomItem("result.variants." + variant);
                if(customItem != null && !customItem.getType().equals(Material.AIR)){
                    results.add(customItem);
                }
            }
        }
        return results;
    }
}
