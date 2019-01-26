package me.wolfyscript.customcrafting.configs.custom_configs;

import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FurnaceConfig extends Config {

    private String folder;
    private String name;
    private String id;

    public FurnaceConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, "me/wolfyscript/customcrafting/configs/custom_configs", "furnace_config", configAPI.getPlugin().getDataFolder().getPath()+"/recipes/"+folder+"/furnace", name);
        this.id = folder+":"+name;
        this.name = name;
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public float getXP(){
        return (float) getDouble("exp");
    }

    public int getCookingTime(){
        return getInt("cookingtime");
    }

    public ItemStack getSource(){
        return getItem("source");
    }

    public ItemStack getResult(){
        return getItem("result");
    }

    public List<String> getSourceData(){
        return getStringList("source_data");
    }
}
