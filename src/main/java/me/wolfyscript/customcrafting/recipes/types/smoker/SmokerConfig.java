package me.wolfyscript.customcrafting.recipes.types.smoker;

import me.wolfyscript.customcrafting.recipes.types.CookingConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class SmokerConfig extends CookingConfig {

    public SmokerConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, folder, "smoker", name, "smoker");
    }

    public SmokerConfig(String jsonData, ConfigAPI configAPI, String namespace, String key) {
        super(jsonData, configAPI, namespace, key, "smoker", "smoker");
    }

    public SmokerConfig() {
        super("smoker");
    }
}
