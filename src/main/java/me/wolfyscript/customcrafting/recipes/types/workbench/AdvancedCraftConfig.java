package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class AdvancedCraftConfig extends CraftConfig {

    public AdvancedCraftConfig(ConfigAPI configAPI, String folder, String name, String defaultPath, String defaultName, boolean override, String fileType) {
        super(configAPI, folder, "workbench", name, defaultPath, defaultName, override, fileType);
    }

    public AdvancedCraftConfig(ConfigAPI configAPI, String folder, String name, String defaultName, boolean override, String fileType) {
        super(configAPI, folder, "workbench", name, defaultName, override, fileType);
    }

    public AdvancedCraftConfig(ConfigAPI configAPI, String defaultName, String folder, String name, String fileType) {
        this(configAPI, folder, name, defaultName, false, fileType);
    }

    public AdvancedCraftConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        this(configAPI, "craft_config", folder, name, fileType);
    }

    public AdvancedCraftConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, "craft_config", folder, name, "json");
    }

    /*
    Creates a json Memory only Config used for DataBase management!
     */
    public AdvancedCraftConfig(String jsonData, ConfigAPI configAPI, String folder, String name) {
        super(jsonData, configAPI, "workbench", folder, name);
    }

    /*
    Creates a json Memory only Config. can be used for anything. to save it use the linkToFile() method!
     */
    public AdvancedCraftConfig() {
        super("workbench");
    }

    public void setNeedWorkbench(boolean workbench) {
        set("advanced_workbench", workbench);
    }

    public boolean needsAdvancedWorkbench() {
        return getBoolean("advanced_workbench");
    }

    public void setPermission(boolean perm) {
        set("permissions", perm);
    }

    public boolean needsPermission() {
        return getBoolean("permissions");
    }

    @Override
    public void linkToFile(String namespace, String name) {
        setShape(3);
        super.linkToFile(namespace, name);
    }
}
