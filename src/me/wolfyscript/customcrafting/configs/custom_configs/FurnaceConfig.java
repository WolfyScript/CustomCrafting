package me.wolfyscript.customcrafting.configs.custom_configs;

import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FurnaceConfig extends CustomConfig {

    public FurnaceConfig(ConfigAPI configAPI, String defaultName, String folder, String name) {
        super(configAPI, defaultName, folder, "furnace", name);
    }

    public FurnaceConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, "furnace_config", folder, name);
    }

    public void setXP(float xp){
        set("exp", xp);
    }

    public float getXP(){
        return (float) getDouble("exp");
    }

    public void setCookingTime(int time){
        set("cooking_time", time);
    }

    public int getCookingTime(){
        return getInt("cooking_time");
    }

    public void setSource(CustomItem source){
        saveCustomItem("source", source);
    }

    public ItemStack getSource(){
        return getCustomItem("source");
    }

    public void setResult(CustomItem result){
        saveCustomItem("result", result);
    }

    public CustomItem getResult(){
        return getCustomItem("result");
    }

    public void setAdvancedFurnace(boolean furnace){
        set("advanced_furnace", furnace);
    }

    public boolean needsAdvancedFurnace(){
        return getBoolean("advanced_furnace");
    }

    public List<String> getOverrides(){
        return getStringList("override");
    }

    public String getExtend(){
        return getString("extend");
    }
}
