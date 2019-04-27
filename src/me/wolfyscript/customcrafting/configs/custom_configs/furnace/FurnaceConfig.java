package me.wolfyscript.customcrafting.configs.custom_configs.furnace;

import me.wolfyscript.customcrafting.configs.custom_configs.CookingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.config.ConfigAPI;

import java.util.List;

public class FurnaceConfig extends CookingConfig {

    public FurnaceConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, "furnace_config", folder, "furnace", name);
    }
}
