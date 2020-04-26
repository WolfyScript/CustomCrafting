package me.wolfyscript.customcrafting.recipes.types.blast_furnace;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CookingConfig;

public class BlastingConfig extends CookingConfig {

    public BlastingConfig(CustomCrafting customCrafting, String folder, String name) {
        super(customCrafting, folder, "blast_furnace", name, "blast_furnace");
    }

    public BlastingConfig(String jsonData, CustomCrafting customCrafting, String namespace, String key) {
        super(jsonData, customCrafting, namespace, key, "blast_furnace", "blast_furnace");
    }

    public BlastingConfig() {
        super("blast_furnace");
    }
}
