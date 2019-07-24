package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.defaults.WorkbenchCraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.items.ItemConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.JsonConfiguration;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import me.wolfyscript.utilities.api.utils.sql.SQLDataBase;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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

        ItemConfig itemConfig = new ItemConfig(api.getConfigAPI(), "customcrafting", "workbench","me/wolfyscript/customcrafting/configs/custom_configs/defaults", "workbench_item", mainConfig.resetAdvancedWorkbenchItem(), "yml");
        mainConfig.setResetAdvancedWorkbenchItem(false);

        CustomConfig config = new WorkbenchCraftConfig(api.getConfigAPI(), mainConfig.resetAdvancedWorkbenchRecipe());
        mainConfig.setResetAdvancedWorkbenchRecipe(false);

        ItemConfig knowledgebookItem = new ItemConfig(api.getConfigAPI(), "customcrafting", "knowledge_book","me/wolfyscript/customcrafting/configs/custom_configs/defaults", "knowledge_book_item", mainConfig.resetKnowledgeBookItem(), "yml");
        mainConfig.setResetKnowledgeBookItem(false);

        CraftConfig knowledgebook = new CraftConfig(configAPI, "customcrafting", "knowledge_book", "me/wolfyscript/customcrafting/configs/custom_configs/defaults", "knowledge_book_recipe", mainConfig.resetKnowledgeBookRecipe(), "yml");
        mainConfig.setResetKnowledgeBookRecipe(false);
    }

    public void loadLang() {
        String chosenLang = CustomCrafting.getConfigHandler().getConfig().getString("language");
        Config langConf;
        if (CustomCrafting.getInst().getResource("me/wolfyscript/customcrafting/configs/lang/" + chosenLang + ".yml") != null) {
            langConf = new Config(configAPI, instance.getDataFolder().getPath() + "/lang", chosenLang,"me/wolfyscript/customcrafting/configs/lang", chosenLang, "yml", true);
        } else {
            langConf = new Config(configAPI, instance.getDataFolder().getPath() + "/lang", chosenLang,"me/wolfyscript/customcrafting/configs/lang", "en_US", "yml", false);
        }
        langConf.loadDefaults();
        System.out.println("Loading language \"" + chosenLang + "\" v" + langConf.getString("version") + " translated by " + langConf.getString("author"));

        languageAPI.registerLanguage(new Language(chosenLang, langConf, configAPI));

        //Config config = new Config(configAPI, instance.getDataFolder().getPath() + "/lang", "en_US","me/wolfyscript/customcrafting/configs/lang", "en_US", "json", false);

        /*
        SQLDataBase sqlDataBase = new SQLDataBase(api, "localhost", "minecraft_plugins", "minecraft", "DataB4plugins", 3306);
        sqlDataBase.openConnection();

        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () ->{
            try {
                PreparedStatement preparedStatement = sqlDataBase.getPreparedStatement("CREATE TABLE IF NOT EXISTS customcrafting_recipes (namespace VARCHAR(255), recipe_key VARCHAR(255), json MEDIUMTEXT)");
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 200);
        */
    }

    public MainConfig getConfig() {
        return mainConfig;
    }
}
