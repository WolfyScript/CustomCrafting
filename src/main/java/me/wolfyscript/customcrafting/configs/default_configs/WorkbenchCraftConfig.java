package me.wolfyscript.customcrafting.configs.default_configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class WorkbenchCraftConfig extends CraftConfig {

    public WorkbenchCraftConfig(ConfigAPI configAPI) {
        super(configAPI, "customcrafting", "workbench", "me/wolfyscript/customcrafting/configs/default_configs", "workbench_craft", CustomCrafting.getConfigHandler().getConfig().resetAdvancedWorkbenchRecipe(), "json");
    }
}
