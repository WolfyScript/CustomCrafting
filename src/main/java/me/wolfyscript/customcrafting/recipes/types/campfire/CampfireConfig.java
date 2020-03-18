package me.wolfyscript.customcrafting.recipes.types.campfire;

import me.wolfyscript.customcrafting.recipes.types.CookingConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class CampfireConfig extends CookingConfig {

    public CampfireConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, folder, "campfire", name, "campfire");
    }

    public CampfireConfig(String jsonData, ConfigAPI configAPI, String namespace, String key) {
        super(jsonData, configAPI, namespace, key, "campfire", "campfire");
    }

    public CampfireConfig() {
        super("campfire");
    }
}
