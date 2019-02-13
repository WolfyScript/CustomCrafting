package me.wolfyscript.customcrafting.configs.custom_configs;

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

    public boolean needsAdvancedFurnace(){
        return getBoolean("advanced_furnace");
    }
}
