package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CraftConfig extends RecipeConfig {

    private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

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
        this(configAPI, "craft_config", type, folder, name, "json");
    }

    /*
    Creates a json Memory only Config used for DataBase management!
     */
    public CraftConfig(String jsonData, ConfigAPI configAPI, String type, String folder, String name) {
        super(jsonData, configAPI, folder, type, name, "craft_config");
    }

    /*
   Creates a json Memory only Config. can be used for anything. to save it use the linkToFile() method!
    */
    public CraftConfig(String type) {
        super(type, "craft_config");
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

    public void setShape(int gridSize) {
        Map<Character, List<CustomItem>> ingredients = getIngredients();
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
        setShape(shape);
    }

    public String[] getShape() {
        return getStringList("shape").toArray(new String[0]);
    }

    public void setIngredients(Map<Character, List<CustomItem>> ingredients) {
        getMap().remove("ingredients");
        for (Map.Entry<Character, List<CustomItem>> entry : ingredients.entrySet()) {
            char key = entry.getKey();
            List<CustomItem> ingredient = entry.getValue();
            int variant = 0;
            if (!InventoryUtils.isEmpty(new ArrayList<>(ingredient))) {
                for (CustomItem customItem : ingredient) {
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
        Map<String, Object> values = getValues("ingredients");
        if (values != null && !values.isEmpty()) {
            Set<String> keys = values.keySet();
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
        }
        return result;
    }

    public void setIngredient(char key, CustomItem itemStack) {
        setIngredient(key, 0, itemStack);
    }

    public void setIngredient(int slot, int variant, CustomItem customItem) {
        setIngredient(LETTERS[slot], variant, customItem);
    }

    public void setIngredient(char key, int variant, CustomItem itemStack) {
        List<CustomItem> ingredient = getIngredients(key);
        if (variant < ingredient.size()) {
            if (itemStack.getType().equals(Material.AIR)) {
                ingredient.remove(variant);
            } else {
                ingredient.set(variant, itemStack);
            }
        } else {
            if (!itemStack.getType().equals(Material.AIR)) {
                ingredient.add(itemStack);
            }
        }
        Map<Character, List<CustomItem>> ingredients = getIngredients();
        ingredients.put(key, ingredient);
        System.out.println("ingred.: " + ingredients);
        setIngredients(ingredients);
    }

    public void setIngredients(char key, List<CustomItem> ingredient) {
        Map<Character, List<CustomItem>> ingredients = getIngredients();
        ingredients.put(key, ingredient);
        setIngredients(ingredients);
    }

    public void setIngredients(int slot, List<CustomItem> ingredients) {
        setIngredients(LETTERS[slot], ingredients);
    }

    public void addIngredient(char key, CustomItem itemStack) {
        List<CustomItem> ingredient = getIngredients(key);
        ingredient.add(itemStack);
        setIngredients(key, ingredient);
    }

    public void addIngredient(int slot, CustomItem itemStack) {
        addIngredient(LETTERS[slot], itemStack);
    }

    public void setIngredient(int slot, CustomItem itemStack) {
        setIngredient(LETTERS[slot], itemStack);
    }

    public void setIngredients(List<ItemStack> ingredients) {
        for (int i = 0; i < ingredients.size(); i++) {
            CustomItem customItem = CustomItem.getByItemStack(ingredients.get(i));
            if (customItem.getType().equals(Material.AIR)) {
                setIngredients(i, new ArrayList<>(Collections.singleton(customItem)));
            } else {
                setIngredient(i, customItem);
            }
        }
    }

    public void setResult(int variant, CustomItem result) {
        List<CustomItem> results = getResult();
        if (variant < getResult().size()) {
            results.set(variant, result);
        } else {
            results.add(result);
        }
        setResult(results);
    }

    public List<CustomItem> getIngredients(char key) {
        return getIngredients().getOrDefault(key, new ArrayList<>(Collections.singleton(new CustomItem(Material.AIR))));
    }

    public List<CustomItem> getIngredients(int slot) {
        return getIngredients(LETTERS[slot]);
    }

    public CustomItem getIngredient(char key) {
        if (getIngredients(key).size() > 0) {
            return getIngredients(key).get(0);
        }
        return null;
    }

    public CustomItem getIngredient(int slot) {
        return getIngredient(LETTERS[slot]);
    }

    public boolean mirrorHorizontal() {
        return getBoolean("mirror.horizontal");
    }

    public void setMirrorHorizontal(boolean mirror) {
        set("mirror.horizontal", mirror);
    }

    public boolean mirrorVertical() {
        return getBoolean("mirror.vertical");
    }

    public void setMirrorVertical(boolean mirror) {
        set("mirror.vertical", mirror);
    }
}
