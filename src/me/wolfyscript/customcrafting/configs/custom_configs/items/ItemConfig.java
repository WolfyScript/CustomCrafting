package me.wolfyscript.customcrafting.configs.custom_configs.items;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

    public void setCustomItem(CustomItem itemStack){
        saveItem("item", itemStack);
        setBurnTime(itemStack.getBurnTime());
        //TODO: CHANGE IN 1.14!
        if(itemStack.getAllowedBlocks().isEmpty()){
            setAllowedBlocks(new ArrayList<>(Collections.singleton(Material.FURNACE)));
        }else{
            setAllowedBlocks(itemStack.getAllowedBlocks());
        }
    }

    public void setAllowedBlocks(ArrayList<Material> furnaces){
        List<String> mats = new ArrayList<>();
        furnaces.forEach(material -> mats.add(material.name().toLowerCase(Locale.ROOT)));
        set("fuel.allowed_blocks", mats);
    }

    public ArrayList<Material> getAllowedBlocks(){
        ArrayList<Material> furnaces = new ArrayList<>();
        if(getStringList("fuel.allowed_blocks") != null){
            getStringList("fuel.allowed_blocks").forEach(s -> {
                Material material = Material.matchMaterial(s);
                if(material != null){
                    furnaces.add(material);
                }
            });
        }
        return furnaces;
    }

    public void setBurnTime(int burntime){
        set("fuel.burntime", burntime);
    }

    public int getBurnTime(){
        return getInt("fuel.burntime");
    }
}
