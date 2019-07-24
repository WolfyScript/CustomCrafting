package me.wolfyscript.customcrafting.configs.custom_configs.smoker;

import me.wolfyscript.customcrafting.configs.custom_configs.CookingConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class SmokerConfig extends CookingConfig {

    public SmokerConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        super(configAPI, folder, "smoker", name, "smoker", fileType);
    }

    public SmokerConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, folder, "smoker", name, "smoker");
    }
}
