package me.wolfyscript.customcrafting.configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MainConfig extends Config {

    public MainConfig(ConfigAPI configAPI) {
        super(configAPI, "me/wolfyscript/customcrafting/configs", CustomCrafting.getInst().getDataFolder().getPath(), "main_config");
    }

    @Override
    public void init() {
        loadDefaults();
        configAPI.registerConfig(this);
    }

    public boolean isAdvancedWorkbenchEnabled() {
        return getBoolean("workbench.enable");
    }

    public boolean resetAdvancedWorkbenchItem(){
        return getBoolean("workbench.reset_item");
    }

    public void setResetAdvancedWorkbenchItem(boolean reset){
        set("workbench.reset_item", reset);
    }

    public boolean resetAdvancedWorkbenchRecipe(){
        return getBoolean("workbench.reset_recipe");
    }

    public void setResetAdvancedWorkbenchRecipe(boolean reset){
        set("workbench.reset_recipe", reset);
    }

    public int getAutosaveInterval() {
        return getInt("data.auto_save.interval");
    }

    public boolean isAutoSaveMesage() {
        return getBoolean("data.auto_save.message");
    }

    public List<String> getDisabledRecipes() {
        return getStringList("recipes.disabled_recipes");
    }

    public void setDisabledrecipes(List<String> recipes) {
        set("recipes.disabled_recipes", recipes);
    }

    public boolean displayContents() {
        return getBoolean("workbench.contents.display_items");
    }

    public List<String> getCommandsSuccessCrafted() {
        return getStringList("workbench.commands.successful_craft");
    }

    public List<String> getCommandsDeniedCraft() {
        return getStringList("workbench.commands.denied_craft");
    }

    public boolean isCCenabled() {
        return getBoolean("commands.cc");
    }

    public boolean isLockedDown(){
        return getBoolean("recipes.lockdown");
    }

    public void toggleLockDown(){
        setLockDown(!isLockedDown());
    }

    public void setLockDown(boolean lockdown){
        set("recipes.lockdown", lockdown);
    }

}
