package me.wolfyscript.customcrafting.configs.custom_configs.items;

import me.wolfyscript.customcrafting.CustomCrafting;
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

    public ItemConfig(ConfigAPI configAPI, String defaultpath, String defaultName, String folder, String name, boolean override) {
        super(configAPI, defaultpath, defaultName, folder, "items", name, override);
    }

    public ItemConfig(ConfigAPI configAPI, String defaultName, String folder, String name, boolean override) {
        super(configAPI, defaultName, folder, "items", name, override);
    }

    public ItemConfig(ConfigAPI configAPI, String defaultName, String folder, String name) {
        this(configAPI, defaultName, folder, name, false);
    }

    public ItemConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, "item", folder, name);
    }

    public ItemStack getCustomItem(boolean replaceLang){
        return getItem("item", replaceLang);
    }

    public ItemStack getCustomItem(){
        return getCustomItem(true);
    }

    public void setCustomItem(CustomItem itemStack){
        saveItem("item", itemStack);
        setBurnTime(itemStack.getBurnTime());
        setConsumed(itemStack.isConsumed());
        if(itemStack.getReplacement() != null){
            setReplacementItem(itemStack.getReplacement());
        }
        setDurabilityCost(itemStack.getDurabilityCost());
        if(itemStack.getAllowedBlocks().isEmpty()){
            setAllowedBlocks(new ArrayList<>(Collections.singleton(Material.FURNACE)));
        }else{
            setAllowedBlocks(itemStack.getAllowedBlocks());
        }
    }

    public void setItem(ItemStack itemStack){
        saveItem("item", itemStack);
    }

    public void setDurabilityCost(int durabilityCost){
        set("durability_cost", durabilityCost);
    }

    public int getDurabilityCost(){
        return getInt("durability_cost");
    }

    public void setConsumed(boolean consumed){
        set("consumed", consumed);
    }

    public boolean isConsumed(){
        return getBoolean("consumed");
    }

    public void setReplacementItem(CustomItem customItem){
        if(customItem != null){
            if(!customItem.getId().isEmpty() && !customItem.getId().equals("NULL")){
                set("replacement.item_key", customItem.getId());
            }else {
                saveItem("replacement.item", customItem);
            }
        }
    }

    public CustomItem getReplacementItem(){
        String id = getString("replacement.item_key");
        if(id != null && !id.isEmpty()){
            return CustomCrafting.getRecipeHandler().getCustomItem(id);
        }else if(getConfig().get("replacement.item") != null){
            return new CustomItem(getItem("replacement.item"));
        }
        return null;
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
