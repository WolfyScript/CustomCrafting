package me.wolfyscript.customcrafting.configs.default_configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class KnowledgeBookCraftConfig extends CraftConfig {

    public KnowledgeBookCraftConfig(ConfigAPI configAPI) {
        super(configAPI, "customcrafting", "knowledge_book", "me/wolfyscript/customcrafting/configs/default_configs", "knowledge_book_recipe", CustomCrafting.getConfigHandler().getConfig().resetAdvancedWorkbenchRecipe(), "json");
    }
}
