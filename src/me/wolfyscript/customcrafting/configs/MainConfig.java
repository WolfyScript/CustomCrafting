package me.wolfyscript.customcrafting.configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MainConfig extends Config {

    public MainConfig(ConfigAPI configAPI) {
        super(configAPI, "me/wolfyscript/customcrafting/configs", CustomCrafting.getInst().getDataFolder().getPath(),"main_config");
    }

    @Override
    public void init() {
        loadDefaults();
        configAPI.registerConfig(this);
    }

    public int getAutosaveInterval(){
        return getInt("data.auto_save.interval");
    }

    public List<String> getDisabledRecipes(){
        return getStringList("recipes.disabled_recipes");
    }

    public void setDisabledrecipes(List<String> recipes){
        set("recipes.disabled_recipes", recipes);
    }

    public boolean saveContents(){
        return getBoolean("workbench.contents.save_contents");
    }

    public boolean displayContents(){
        return getBoolean("workbench.contents.display_items");
    }

    public boolean displayOnlyAdvanced(){
        return getBoolean("workbench.contents.only_advanced_workbenches");
    }

    public List<String> getCommandsSuccessCrafted(){
        return getStringList("workbench.commands.successful_craft");
    }

    public List<String> getCommandsDeniedCraft(){
        return getStringList("workbench.commands.denied_craft");
    }


}
