package me.wolfyscript.customcrafting.configs.custom_configs.campfire;

import me.wolfyscript.customcrafting.configs.custom_configs.CookingConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class CampfireConfig extends CookingConfig {

    public CampfireConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, folder, "campfire", name, "campfire");
    }

    public CampfireConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        super(configAPI, folder, "campfire", name, "campfire", fileType);
    }
}
