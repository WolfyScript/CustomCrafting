package me.wolfyscript.customcrafting.configs.default_configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;

public class WorkbenchCraftConfig extends CraftConfig {

    public WorkbenchCraftConfig(CustomCrafting customCrafting) {
        super(customCrafting, "customcrafting", "workbench", "advanced_workbench", "me/wolfyscript/customcrafting/configs/default_configs", "workbench_craft", customCrafting.getConfigHandler().getConfig().resetAdvancedWorkbenchRecipe());
    }
}
