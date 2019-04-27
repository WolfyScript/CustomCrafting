package me.wolfyscript.customcrafting.configs.custom_configs.campfire;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class CampfireConfig extends CustomConfig {

    public CampfireConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, "campfire", folder, "campfire", name);
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

    public CustomItem getSource(){
        return getCustomItem("source");
    }

    public void setResult(CustomItem result){
        saveCustomItem("result", result);
    }

    public CustomItem getResult(){
        return getCustomItem("result");
    }

}
