package me.wolfyscript.customcrafting.configs.custom_configs.defaults;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class WorkbenchCraftConfig extends CraftConfig {

    public WorkbenchCraftConfig(ConfigAPI configAPI, boolean override) {
        super(configAPI, "customcrafting", "workbench", "me/wolfyscript/customcrafting/configs/custom_configs/defaults", "workbench_craft", override, CustomCrafting.getConfigHandler().getConfig().getPreferredFileType());
    }
}
