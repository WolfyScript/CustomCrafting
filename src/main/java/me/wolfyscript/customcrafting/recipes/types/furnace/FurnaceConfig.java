package me.wolfyscript.customcrafting.recipes.types.furnace;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CookingConfig;

public class FurnaceConfig extends CookingConfig {

    public FurnaceConfig(CustomCrafting customCrafting, String folder, String name) {
        super(customCrafting, folder, "furnace", name, "furnace_config");
    }

    public FurnaceConfig(String jsonData, CustomCrafting customCrafting, String namespace, String key) {
        super(jsonData, customCrafting, namespace, key, "furnace", "furnace_config");
    }

    public FurnaceConfig() {
        super("furnace", "furnace_config");
    }
}
