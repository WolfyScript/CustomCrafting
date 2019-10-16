package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.utils.InventoryUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CraftConfig extends RecipeConfig {

    public CraftConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultPath, String defaultName, boolean override, String fileType) {
        super(configAPI, folder, type, name, defaultPath, defaultName, override, fileType);
    }

    public CraftConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName, boolean override, String fileType) {
        super(configAPI, folder, type, name, defaultName, override, fileType);
    }

    public CraftConfig(ConfigAPI configAPI, String defaultName, String type, String folder, String name, String fileType) {
        this(configAPI, folder, type, name, defaultName, false, fileType);
    }

    public CraftConfig(ConfigAPI configAPI, String type, String folder, String name) {
        this(configAPI, "craft_config", type, folder, name, CustomCrafting.getConfigHandler().getConfig().getPreferredFileType());
    }

    /*
    Creates a json Memory only Config used for DataBase management!
     */
    public CraftConfig(String jsonData, ConfigAPI configAPI, String type, String folder, String name) {
        super(jsonData, configAPI, folder, type, name, "craft_config");
    }

    public void setShapeless(boolean shapeless) {
        set("shapeless", shapeless);
    }

    public boolean isShapeless() {
        return getBoolean("shapeless");
    }

    public void setShape(String... shape) {
        set("shape", shape);
    }

    public String[] getShape() {
        return getStringList("shape").toArray(new String[0]);
    }

    public void setResult(List<CustomItem> results) {
        saveCustomItem("result", results.get(0));
        for (int i = 1; i < results.size(); i++) {
            if(!results.get(i).getType().equals(Material.AIR)){
                saveCustomItem("result.variants.var" + i, results.get(i));
            }
        }
    }

    public List<CustomItem> getResult() {
        List<CustomItem> results = new ArrayList<>();
        results.add(getCustomItem("result"));
        if (get("result.variants") != null) {
            Set<String> variants = getValues("result.variants").keySet();
            for (String variant : variants) {
                CustomItem customItem = getCustomItem("result.variants." + variant);
                if(customItem != null && !customItem.getType().equals(Material.AIR)){
                    results.add(customItem);
                }
            }
        }
        return results;
    }

    public void setIngredients(HashMap<Character, List<CustomItem>> ingredients) {
        set("ingredients", new HashMap<String, Object>());
        for (char key : ingredients.keySet()) {
            int variant = 0;
            if (!InventoryUtils.isEmpty(new ArrayList<>(ingredients.get(key)))) {
                for (CustomItem customItem : ingredients.get(key)) {
                    saveCustomItem("ingredients." + key + ".var" + (variant++), customItem);
                }
            } else {
                for (CustomItem customItem : ingredients.get(key)) {
                    if (customItem != null && !customItem.getType().equals(Material.AIR)) {
                        saveCustomItem("ingredients." + key + ".var" + (variant++), customItem);
                    }
                }
            }
        }
    }

    public HashMap<Character, List<CustomItem>> getIngredients() {
        HashMap<Character, List<CustomItem>> result = new HashMap<>();
        Set<String> keys = getValues("ingredients").keySet();
        for (String key : keys) {
            Set<String> itemKeys = getValues("ingredients." + key).keySet();
            List<CustomItem> data = new ArrayList<>();
            for (String itemKey : itemKeys) {
                CustomItem itemStack;
                itemStack = getCustomItem("ingredients." + key + "." + itemKey);
                data.add(itemStack);
            }
            result.put(key.charAt(0), data);
        }
        return result;
    }

}
