package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.default_configs.KnowledgeBookCraftConfig;
import me.wolfyscript.customcrafting.configs.default_configs.WorkbenchCraftConfig;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.JsonConfiguration;
import me.wolfyscript.utilities.api.custom_items.ItemConfig;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import org.bukkit.plugin.Plugin;

public class ConfigHandler {

    private Plugin instance;
    private WolfyUtilities api;
    private ConfigAPI configAPI;
    private LanguageAPI languageAPI;
    private MainConfig mainConfig;

    public ConfigHandler(WolfyUtilities api) {
        this.api = api;
        this.instance = api.getPlugin();
        this.configAPI = api.getConfigAPI();
        this.languageAPI = api.getLanguageAPI();
    }

    public void load() {
        this.mainConfig = new MainConfig(configAPI);
        mainConfig.loadDefaults();
        configAPI.registerConfig(mainConfig);
        loadLang();

        ItemConfig itemConfig = new ItemConfig("customcrafting", "workbench", "me/wolfyscript/customcrafting/configs/default_configs", WolfyUtilities.hasVillagePillageUpdate() ? "workbench_item" : "workbench_item_13", mainConfig.resetAdvancedWorkbenchItem(), api.getConfigAPI());
        mainConfig.setResetAdvancedWorkbenchItem(false);

        RecipeConfig config = new WorkbenchCraftConfig(api.getConfigAPI());
        mainConfig.setResetAdvancedWorkbenchRecipe(false);

        ItemConfig knowledgebookItem = new ItemConfig("customcrafting", "knowledge_book", "me/wolfyscript/customcrafting/configs/default_configs", WolfyUtilities.hasVillagePillageUpdate() ? "knowledge_book_item" : "knowledge_book_item_13", mainConfig.resetKnowledgeBookItem(), api.getConfigAPI());
        mainConfig.setResetKnowledgeBookItem(false);

        CraftConfig knowledgebook = new KnowledgeBookCraftConfig(api.getConfigAPI());
        mainConfig.setResetKnowledgeBookRecipe(false);

        api.getConfigAPI().setPrettyPrinting(mainConfig.isPrettyPrinting());
    }

    public void loadLang() {
        String chosenLang = CustomCrafting.getConfigHandler().getConfig().getString("language");
        JsonConfiguration langConf;
        if (CustomCrafting.getInst().getResource("me/wolfyscript/customcrafting/configs/lang/" + chosenLang + ".json") != null) {
            System.out.println("Default language: load latest language");
            langConf = new JsonConfiguration(configAPI, instance.getDataFolder().getPath() + "/lang", chosenLang, "me/wolfyscript/customcrafting/configs/lang", chosenLang, true);
        } else {
            System.out.println("Custom language: loading default values");
            langConf = new JsonConfiguration(configAPI, instance.getDataFolder().getPath() + "/lang", chosenLang, "me/wolfyscript/customcrafting/configs/lang", "en_US", false);
        }
        langConf.loadDefaults();
        System.out.println("Loaded language \"" + chosenLang + "\" v" + langConf.getString("version") + " translated by " + langConf.getString("author"));

        languageAPI.registerLanguage(new Language(chosenLang, langConf, configAPI));
    }

    public MainConfig getConfig() {
        return mainConfig;
    }
}
