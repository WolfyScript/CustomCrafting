package me.wolfyscript.customcrafting.recipes.types.campfire;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CookingConfig;

public class CampfireConfig extends CookingConfig {

    public CampfireConfig(CustomCrafting customCrafting, String folder, String name) {
        super(customCrafting, folder, "campfire", name, "campfire");
    }

    public CampfireConfig(String jsonData, CustomCrafting customCrafting, String namespace, String key) {
        super(jsonData, customCrafting, namespace, key, "campfire", "campfire");
    }

    public CampfireConfig(CustomCrafting customCrafting) {
        super(customCrafting, "campfire");
    }
}
