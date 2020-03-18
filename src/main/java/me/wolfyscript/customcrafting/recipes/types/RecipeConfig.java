package me.wolfyscript.customcrafting.recipes.types;

import com.google.gson.JsonObject;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.crafting.RecipeUtils;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomConfig;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RecipeConfig extends CustomConfig {

    private String type;

    public RecipeConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName) {
        this(configAPI, folder, type, name, "me/wolfyscript/customcrafting/recipes/types/" + type, defaultName, false);
    }

    public RecipeConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName, boolean override) {
        this(configAPI, folder, type, name, "me/wolfyscript/customcrafting/recipes/types/" + type, defaultName, override);
    }

    public RecipeConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultPath, String defaultName, boolean override) {
        super(configAPI, folder, name, configAPI.getApi().getPlugin().getDataFolder() + "/recipes/" + folder + "/" + type, defaultPath, defaultName, override);
        this.type = type;
        setPathSeparator('.');
    }

    /*
    Memory Config only! Do not use to load config out of files!
     */
    public RecipeConfig(String jsonData, ConfigAPI configAPI, String folder, String type, String name, String defaultName) {
        super(jsonData, configAPI, folder, name, "me/wolfyscript/customcrafting/recipes/types/" + type, defaultName);
        this.type = type;
        setPathSeparator('.');
    }

    /*
    Memory Config only! Do not use to load config out of files!

    public RecipeConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName) {
        super(configAPI, folder, name, "me/wolfyscript/customcrafting/recipes/types/" + type, defaultName);
        this.type = type;
        setPathSeparator('.');
    } */

    @Override
    public void init() {
        super.init();
    }

    /*
            Creates a json Memory only Config. can be used for anything. to save it use the linkToFile(String, String, String) method!
        */
    public RecipeConfig(String type, String defaultName) {
        super(CustomCrafting.getApi().getConfigAPI(), "", "", "me/wolfyscript/customcrafting/recipes/types/" + type, defaultName);
        this.type = type;
        setPathSeparator('.');
    }

    public void linkToFile(String namespace, String name) {
        super.linkToFile(namespace, name, configAPI.getApi().getPlugin().getDataFolder() + "/recipes/" + namespace + "/" + type);
    }

    public boolean saveConfig(String namespace, String key, Player player) {
        namespace = namespace.toLowerCase(Locale.ROOT).replace(" ", "_");
        key = key.toLowerCase(Locale.ROOT).replace(" ", "_");
        if (!RecipeUtils.testNameSpaceKey(namespace, key)) {
            api.sendPlayerMessage(player, "&cInvalid Namespace or Key! Namespaces & Keys may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
            return false;
        }
        linkToFile(namespace, key);
        if (CustomCrafting.hasDataBaseHandler()) {
            CustomCrafting.getDataBaseHandler().updateRecipe(this, false);
        } else {
            reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
        }
        api.sendPlayerMessage(player, "recipe_creator", "save.success");
        api.sendPlayerMessage(player, "§6" + (type.equalsIgnoreCase("item") ? "items" : "recipes") + "/" + namespace + "/" + type + "/" + key);
        return true;
    }

    public RecipeConfig(String type) {
        this(type, type);
    }

    public String getConfigType() {
        return type;
    }

    public void setGroup(String group) {
        set("group", group);
    }

    public String getGroup() {
        return getString("group");
    }

    public void setExactMeta(boolean exactMeta) {
        set("exactItemMeta", exactMeta);
    }

    public boolean isExactMeta() {
        return getBoolean("exactItemMeta");
    }

    public Conditions getConditions() {
        Conditions object = get(Conditions.class, "conditions", new Conditions());
        return object;
    }

    public void setConditions(Conditions conditions) {
        set("conditions", conditions);
    }

    public RecipePriority getPriority() {
        if (getString("priority") != null) {
            try {
                return RecipePriority.valueOf(getString("priority"));
            } catch (IllegalArgumentException e) {
                return RecipePriority.NORMAL;
            }
        }
        return RecipePriority.NORMAL;
    }

    public void setPriority(RecipePriority recipePriority) {
        set("priority", recipePriority.name());
    }

    public void setResult(List<CustomItem> result) {
        setResult("", result);
    }

    protected void setResult(String path, List<CustomItem> results) {
        if (!path.isEmpty() && !path.endsWith(".")) {
            path = path + ".";
        }
        set(path + "result", new JsonObject());
        saveCustomItem(path + "result", !results.isEmpty() && !ItemUtils.isAirOrNull(results.get(0)) ? results.get(0) : null);
        for (int i = 1; i < results.size(); i++) {
            if (!results.get(i).getType().equals(Material.AIR)) {
                saveCustomItem(path + "result.variants.var" + i, results.get(i));
            }
        }
    }

    public List<CustomItem> getResult() {
        return getResult("");
    }

    protected List<CustomItem> getResult(String path) {
        if (!path.isEmpty() && !path.endsWith(".")) {
            path = path + ".";
        }
        List<CustomItem> results = new ArrayList<>();
        results.add(getCustomItem(path + "result"));
        if (get(path + "result.variants") != null) {
            Set<String> variants = getValues(path + "result.variants").keySet();
            for (String variant : variants) {
                CustomItem customItem = getCustomItem(path + "result.variants." + variant);
                if (customItem != null && !customItem.getType().equals(Material.AIR)) {
                    results.add(customItem);
                }
            }
        }
        return results;
    }

    public void setHidden(boolean hidden) {
        set("hidden", hidden);
    }

    public boolean isHidden() {
        return getBoolean("hidden");
    }
}
