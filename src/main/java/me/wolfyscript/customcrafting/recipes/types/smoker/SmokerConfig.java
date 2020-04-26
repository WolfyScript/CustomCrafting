package me.wolfyscript.customcrafting.recipes.types.smoker;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CookingConfig;

public class SmokerConfig extends CookingConfig {

    public SmokerConfig(CustomCrafting customCrafting, String folder, String name) {
        super(customCrafting, folder, "smoker", name, "smoker");
    }

    public SmokerConfig(String jsonData, CustomCrafting customCrafting, String namespace, String key) {
        super(jsonData, customCrafting, namespace, key, "smoker", "smoker");
    }

    public SmokerConfig() {
        super("smoker");
    }
}
