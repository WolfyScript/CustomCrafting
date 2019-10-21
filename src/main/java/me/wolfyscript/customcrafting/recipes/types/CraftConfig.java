package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.utils.InventoryUtils;
import org.bukkit.Material;

import java.util.*;

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

    public void setIngredients(Map<Character, List<CustomItem>> ingredients) {
        set("ingredients", new TreeMap<String, Object>());
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

    public Map<Character, List<CustomItem>> getIngredients() {
        Map<Character, List<CustomItem>> result = new TreeMap<>();
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

    public void saveRecipe(int gridSize, PlayerCache cache){
        Workbench workbench = cache.getWorkbench();
        api.sendDebugMessage("Create Config:");
        api.sendDebugMessage("  id: " + getId());
        api.sendDebugMessage("  Conditions: " + workbench.getConditions().toMap());
        api.sendDebugMessage("  Shapeless: " + workbench.isShapeless());
        api.sendDebugMessage("  ExactMeta: " + workbench.isExactMeta());
        api.sendDebugMessage("  Priority: " + workbench.getPriority());
        api.sendDebugMessage("  Result: " + workbench.getResult());
        setShapeless(workbench.isShapeless());
        setExactMeta(workbench.isExactMeta());
        setPriority(workbench.getPriority());
        setConditions(workbench.getConditions());

        setResult(workbench.getResult());
        Map<Character, List<CustomItem>> ingredients = workbench.getIngredients();
        api.sendDebugMessage("  Ingredients: " + ingredients);
        String[] shape = new String[gridSize];
        int index = 0;
        int row = 0;
        for (char ingrd : ingredients.keySet()) {
            List<CustomItem> keyItems = ingredients.get(ingrd);
            if (InventoryUtils.isEmpty(new ArrayList<>(keyItems))) {
                if (shape[row] != null) {
                    shape[row] = shape[row] + " ";
                } else {
                    shape[row] = " ";
                }
            } else {
                if (shape[row] != null) {
                    shape[row] = shape[row] + ingrd;
                } else {
                    shape[row] = String.valueOf(ingrd);
                }
            }
            index++;
            if ((index % gridSize) == 0) {
                row++;
            }
        }
        api.sendDebugMessage("  Shape:");
        for (String shapeRow : shape) {
            api.sendDebugMessage("      " + shapeRow);
        }
        setShape(shape);
        setIngredients(workbench.getIngredients());
        api.sendDebugMessage("Saving...");

        if (CustomCrafting.hasDataBaseHandler()) {
            CustomCrafting.getDataBaseHandler().updateRecipe(this, false);
        } else {
            reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
        }
        api.sendDebugMessage("Reset GUI cache...");
        cache.resetWorkbench();
    }

}
