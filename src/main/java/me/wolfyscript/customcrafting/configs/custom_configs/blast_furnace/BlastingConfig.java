package me.wolfyscript.customcrafting.configs.custom_configs.blast_furnace;

import me.wolfyscript.customcrafting.configs.custom_configs.CookingConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class BlastingConfig extends CookingConfig {

    public BlastingConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, folder, "blast_furnace", name, "blast_furnace");
    }

    public BlastingConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        super(configAPI, folder, "blast_furnace", name, "blast_furnace", fileType);
    }

    public BlastingConfig(String jsonData, ConfigAPI configAPI, String namespace, String key) {
        super(jsonData, configAPI, namespace, key, "blast_furnace", "blast_furnace");
    }
}
