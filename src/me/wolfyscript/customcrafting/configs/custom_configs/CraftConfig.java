package me.wolfyscript.customcrafting.configs.custom_configs;

import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CraftConfig extends Config {

    private String folder;
    private String name;
    private String id;

    public CraftConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, "me/wolfyscript/customcrafting/configs/custom_configs", "craft_config", configAPI.getPlugin().getDataFolder().getPath()+"/recipes/"+folder+"/workbench", name);
        this.id = folder+":"+name;
        this.name = name;
        this.folder = folder;
    }

    @Override
    public void init() {
        saveAfterSet(true);
        loadDefaults();
    }

    public boolean isShapeless(){
        return getBoolean("shapeless");
    }

    public boolean needPerm(){
        return getBoolean("permissions");
    }

    public boolean needWorkbench(){
        return getBoolean("advanced_workbench");
    }

    public String getGroup(){
        return getString("group");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFolder() {
        return folder;
    }

    public String[] getShape(){
        List<String> list = getStringList("shape");
        String[] shape = new String[list.size()];
        for(int i = 0; i < list.size(); i++){
            shape[i] = list.get(i);
        }
        return shape;
    }

    public ItemStack getResult(){
        return getItem("result");
    }

    public HashMap<Character, HashMap<ItemStack, List<String>>> getIngredients(){
        HashMap<Character, HashMap<ItemStack, List<String>>> result = new HashMap<>();
        Set<String> keys = getConfig().getConfigurationSection("ingredients").getKeys(false);
        for(String key : keys){
            Set<String> itemKeys = getConfig().getConfigurationSection("ingredients."+key).getKeys(false);
            HashMap<ItemStack, List<String>> data = new HashMap<>();
            for(String itemKey : itemKeys){
                List<String> additionalData = getStringList("ingredients."+key+"."+itemKey+".data");
                ItemStack itemStack = getItem("ingredients."+key+"."+itemKey+".item");
                data.put(itemStack, additionalData);
            }
            result.put(key.charAt(0), data);
        }
        return result;
    }
}
