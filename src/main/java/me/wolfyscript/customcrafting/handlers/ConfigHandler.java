package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.defaults.WorkbenchCraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.items.ItemConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
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

        ItemConfig itemConfig = new ItemConfig(api.getConfigAPI(), "customcrafting", "workbench", "me/wolfyscript/customcrafting/configs/custom_configs/defaults", "workbench_item", mainConfig.resetAdvancedWorkbenchItem(), mainConfig.getPreferredFileType());
        mainConfig.setResetAdvancedWorkbenchItem(false);

        CustomConfig config = new WorkbenchCraftConfig(api.getConfigAPI(), mainConfig.resetAdvancedWorkbenchRecipe());
        mainConfig.setResetAdvancedWorkbenchRecipe(false);

        ItemConfig knowledgebookItem = new ItemConfig(api.getConfigAPI(), "customcrafting", "knowledge_book", "me/wolfyscript/customcrafting/configs/custom_configs/defaults", "knowledge_book_item", mainConfig.resetKnowledgeBookItem(), mainConfig.getPreferredFileType());
        mainConfig.setResetKnowledgeBookItem(false);

        CraftConfig knowledgebook = new CraftConfig(configAPI, "customcrafting", "knowledge_book", "me/wolfyscript/customcrafting/configs/custom_configs/defaults", "knowledge_book_recipe", mainConfig.resetKnowledgeBookRecipe(), mainConfig.getPreferredFileType());
        mainConfig.setResetKnowledgeBookRecipe(false);
    }

    public void loadLang() {
        String chosenLang = CustomCrafting.getConfigHandler().getConfig().getString("language");
        Config langConf;
        if (CustomCrafting.getInst().getResource("me/wolfyscript/customcrafting/configs/lang/" + chosenLang + ".json") != null) {
            langConf = new Config(configAPI, instance.getDataFolder().getPath() + "/lang", chosenLang, "me/wolfyscript/customcrafting/configs/lang", chosenLang, "json", true);
        } else {
            langConf = new Config(configAPI, instance.getDataFolder().getPath() + "/lang", chosenLang, "me/wolfyscript/customcrafting/configs/lang", "en_US", "json", false);
        }
        langConf.loadDefaults();
        System.out.println("Loading language \"" + chosenLang + "\" v" + langConf.getString("version") + " translated by " + langConf.getString("author"));

        languageAPI.registerLanguage(new Language(chosenLang, langConf, configAPI));
    }

    public MainConfig getConfig() {
        return mainConfig;
    }
}
