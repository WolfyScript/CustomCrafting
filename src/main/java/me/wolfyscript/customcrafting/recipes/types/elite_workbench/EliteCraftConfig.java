package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;

public class EliteCraftConfig extends CraftConfig {

    public EliteCraftConfig(CustomCrafting customCrafting, String folder, String name, String defaultName, boolean override) {
        super(customCrafting, folder, "elite_workbench", name, defaultName, override);
    }

    public EliteCraftConfig(CustomCrafting customCrafting, String defaultName, String folder, String name) {
        this(customCrafting, folder, name, defaultName, false);
    }

    public EliteCraftConfig(CustomCrafting customCrafting, String folder, String name) {
        this(customCrafting, "craft_config", folder, name);
    }

    public EliteCraftConfig(CustomCrafting customCrafting) {
        super(customCrafting, "elite_workbench");
    }

    /*
    Creates a json Memory only Config used for DataBase management!
     */
    public EliteCraftConfig(String jsonData, CustomCrafting customCrafting, String folder, String name) {
        super(jsonData, customCrafting, "elite_workbench", folder, name);
    }

    @Override
    public void linkToFile(String namespace, String name) {
        setShape(6);
        super.linkToFile(namespace, name);
    }

}
