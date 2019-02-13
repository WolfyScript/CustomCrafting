package me.wolfyscript.customcrafting.configs.custom_configs;

import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemConfig extends CustomConfig {

    public ItemConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, "item", folder, name);
    }

    public ItemConfig(ConfigAPI configAPI, String defaultName, String folder, String name) {
        super(configAPI, defaultName, folder, "items", name);
    }

    public ItemStack getCustomItem(){
        return getItem("item");
    }

    public void setCustomItem(ItemStack itemStack){
        saveItem("item", itemStack);
    }

    public List<String> getData(){
        return getStringList("extra_data");
    }
}
