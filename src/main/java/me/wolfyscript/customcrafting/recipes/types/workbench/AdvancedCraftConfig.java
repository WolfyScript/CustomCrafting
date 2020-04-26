package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;

public class AdvancedCraftConfig extends CraftConfig {

    public AdvancedCraftConfig(CustomCrafting customCrafting, String folder, String name, String defaultName, boolean override) {
        super(customCrafting, folder, "workbench", name, defaultName, override);
    }

    public AdvancedCraftConfig(CustomCrafting customCrafting, String defaultName, String folder, String name) {
        this(customCrafting, folder, name, defaultName, false);
    }

    public AdvancedCraftConfig(CustomCrafting customCrafting, String folder, String name) {
        this(customCrafting, "craft_config", folder, name);
    }

    /*
    Creates a json Memory only Config used for DataBase management!
     */
    public AdvancedCraftConfig(String jsonData, CustomCrafting customCrafting, String folder, String name) {
        super(jsonData, customCrafting, "workbench", folder, name);
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
