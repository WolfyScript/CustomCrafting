package me.wolfyscript.customcrafting.configs.default_configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;

public class KnowledgeBookCraftConfig extends CraftConfig {

    public KnowledgeBookCraftConfig(CustomCrafting customCrafting) {
        super(customCrafting, "customcrafting", "workbench", "knowledge_book", "me/wolfyscript/customcrafting/configs/default_configs", "knowledge_book_recipe", customCrafting.getConfigHandler().getConfig().resetAdvancedWorkbenchRecipe());
    }
}
