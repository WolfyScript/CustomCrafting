package me.wolfyscript.customcrafting.configs.custom_configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CraftConfig extends CustomConfig {

    public CraftConfig(ConfigAPI configAPI, String defaultName, String folder, String name) {
        super(configAPI, defaultName, folder, "workbench", name);
    }

    public CraftConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, "craft_config", folder, name);
    }

    public void setShapeless(boolean shapeless) {
        set("shapeless", shapeless);
    }

    public boolean isShapeless() {
        return getBoolean("shapeless");
    }

    public void setPermission(boolean perm) {
        set("permissions", perm);
    }

    public boolean needPerm() {
        return getBoolean("permissions");
    }

    public void setNeedWorkbench(boolean workbench) {
        set("advanced_workbench", workbench);
    }

    public boolean needWorkbench() {
        return getBoolean("advanced_workbench");
    }

    public void setGroup(String group) {
        set("group", group);
    }

    public String getGroup() {
        return getString("group");
    }

    public void setShape(String... shape) {
        set("shape", WolfyUtilities.formatShape(shape));
    }

    public String[] getShape() {
        List<String> list = getStringList("shape");
        String[] shape = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            shape[i] = list.get(i);
        }
        return shape;
    }

    public void setResult(CustomItem itemStack) {
        saveCustomItem("result", itemStack);
    }

    public CustomItem getResult() {
        return getCustomItem("result");
    }

    public List<String> getResultData() {
        return getStringList("result.data");
    }

    public void setIngredients(HashMap<Character, List<CustomItem>> ingredients) {
        for (char key : ingredients.keySet()) {
            int variant = 0;
            if (!ItemUtils.isEmpty(ingredients.get((char) key))) {
                for (CustomItem customItem : ingredients.get((char) key)) {
                    saveCustomItem("ingredients." + key + ".var" + (variant++), customItem);
                }
            } else {
                for (CustomItem customItem : ingredients.get((char) key)) {
                    if (customItem != null && !customItem.getType().equals(Material.AIR)) {
                        saveCustomItem("ingredients." + key + ".var" + (variant++), customItem);
                    }
                }
            }
        }
    }

    public HashMap<Character, HashMap<ItemStack, List<String>>> getIngredients() {
        HashMap<Character, HashMap<ItemStack, List<String>>> result = new HashMap<>();
        Set<String> keys = getConfig().getConfigurationSection("ingredients").getKeys(false);
        for (String key : keys) {
            Set<String> itemKeys = getConfig().getConfigurationSection("ingredients." + key).getKeys(false);
            HashMap<ItemStack, List<String>> data = new HashMap<>();
            for (String itemKey : itemKeys) {
                ItemStack itemStack;
                List<String> additionalData = new ArrayList<>();
                String id = getString("ingredients." + key + "." + itemKey + ".item_key");
                if (id != null && !id.isEmpty()) {
                    itemStack = CustomCrafting.getRecipeHandler().getCustomItem(id);
                } else {
                    additionalData = getStringList("ingredients." + key + "." + itemKey + ".data");
                    itemStack = getItem("ingredients." + key + "." + itemKey + ".item");
                }
                data.put(itemStack, additionalData);
            }
            result.put(key.charAt(0), data);
        }
        return result;
    }


}
