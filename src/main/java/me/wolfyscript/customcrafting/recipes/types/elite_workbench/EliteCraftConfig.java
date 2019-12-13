package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class EliteCraftConfig extends CraftConfig {

    public EliteCraftConfig(ConfigAPI configAPI, String folder, String name, String defaultPath, String defaultName, boolean override, String fileType) {
        super(configAPI, folder, "elite_workbench", name, defaultPath, defaultName, override, fileType);
    }

    public EliteCraftConfig(ConfigAPI configAPI, String folder, String name, String defaultName, boolean override, String fileType) {
        super(configAPI, folder, "elite_workbench", name, defaultName, override, fileType);
    }

    public EliteCraftConfig(ConfigAPI configAPI, String defaultName, String folder, String name, String fileType) {
        this(configAPI, folder, name, defaultName, false, fileType);
    }

    public EliteCraftConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        this(configAPI, "craft_config", folder, name, fileType);
    }

    public EliteCraftConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, "craft_config", folder, name, "json");
    }

    public EliteCraftConfig() {
        super("elite_workbench");
    }

    /*
    Creates a json Memory only Config used for DataBase management!
     */
    public EliteCraftConfig(String jsonData, ConfigAPI configAPI, String folder, String name) {
        super(jsonData, configAPI, "elite_workbench", folder, name);
    }

    @Override
    public void linkToFile(String namespace, String name) {
        setShape(6);
        super.linkToFile(namespace, name);
    }

}
