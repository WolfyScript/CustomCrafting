package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomConfig;

import java.util.HashMap;
import java.util.Map;

public class RecipeConfig extends CustomConfig {
    private String type;

    public RecipeConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName, String fileType) {
        this(configAPI, folder, type, name, "me/wolfyscript/customcrafting/recipes/types/" + type, defaultName, false, fileType);
    }

    public RecipeConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName, boolean override, String fileType) {
        this(configAPI, folder, type, name, "me/wolfyscript/customcrafting/recipes/types/" + type, defaultName, override, fileType);
    }

    public RecipeConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName, boolean override) {
        this(configAPI, folder, type, name, "me/wolfyscript/customcrafting/recipes/types/" + type, defaultName, override, CustomCrafting.getConfigHandler().getConfig().getPreferredFileType());
    }

    public RecipeConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultPath, String defaultName, boolean override, String fileType) {
        super(configAPI, folder, name, configAPI.getApi().getPlugin().getDataFolder() + "/recipes/" + folder + "/" + type, defaultPath, defaultName, override, fileType);
        this.type = type;
        if (getType().equals(Type.YAML)) {
            setSaveAfterValueSet(true);
        }
        setPathSeparator('.');
    }

    public RecipeConfig(String jsonData, ConfigAPI configAPI, String folder, String type, String name, String defaultName) {
        super(jsonData, configAPI, folder, name, "me/wolfyscript/customcrafting/recipes/types/" + type, defaultName);
        this.type = type;
        setPathSeparator('.');
    }

    public RecipeConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName) {
        super(configAPI, folder, name, "me/wolfyscript/customcrafting/recipes/types/" + type, defaultName);
        this.type = type;
        setPathSeparator('.');
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
        Map<String, String> conditions = (Map<String, String>) get("conditions", new HashMap<>());
        if (conditions != null) {
            return new Conditions(conditions);
        } else {
            Conditions conditions1 = new Conditions();
            //Migrate old settings to new Condition system.
            if (this instanceof AdvancedCraftConfig) {
                conditions1.getByID("advanced_workbench").setOption(((AdvancedCraftConfig) this).needsAdvancedWorkbench() ? Conditions.Option.EXACT : Conditions.Option.IGNORE);
                conditions1.getByID("permission").setOption(((AdvancedCraftConfig) this).needsPermission() ? Conditions.Option.EXACT : Conditions.Option.IGNORE);
            } else if (this instanceof AnvilConfig) {
                conditions1.getByID("permission").setOption(((AnvilConfig) this).needPerm() ? Conditions.Option.EXACT : Conditions.Option.IGNORE);
            }
            return conditions1;
        }
    }

    public void setConditions(Conditions conditions) {
        set("conditions", conditions.toMap());
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
}
