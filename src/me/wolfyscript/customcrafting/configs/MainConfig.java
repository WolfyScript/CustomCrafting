package me.wolfyscript.customcrafting.configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MainConfig extends Config {

    public MainConfig(ConfigAPI configAPI) {
        super(configAPI, "me/wolfyscript/customcrafting/configs", CustomCrafting.getInst().getDataFolder().getPath(),"config");
    }

    @Override
    public void init() {
        loadDefaults();
        configAPI.registerConfig(this);
    }

    public int getAutosaveInterval(){
        return getInt("data.auto_save.interval");
    }

    public List<Material> getVanillaRecipes(){
        List<String> names = getStringList("recipes.disable_vanilla_recipes");
        List<Material> materials = new ArrayList<>();
        for(String name : names){
            Material material = Material.matchMaterial(name);
            if(material != null){
                materials.add(material);
            }else{
                CustomCrafting.getApi().sendConsoleMessage("Error getting Material: "+ name);
            }
        }
        return materials;
    }
}
