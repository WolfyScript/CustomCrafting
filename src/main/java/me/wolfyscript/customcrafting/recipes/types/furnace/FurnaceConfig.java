package me.wolfyscript.customcrafting.recipes.types.furnace;

import me.wolfyscript.customcrafting.recipes.types.CookingConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class FurnaceConfig extends CookingConfig {

    public FurnaceConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, folder, "furnace", name, "furnace_config");
    }

    public FurnaceConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        super(configAPI, folder, "furnace", name, "furnace_config", fileType);
    }

    public FurnaceConfig(String jsonData, ConfigAPI configAPI, String namespace, String key) {
        super(jsonData, configAPI, namespace, key, "furnace", "furnace_config");
    }
}
