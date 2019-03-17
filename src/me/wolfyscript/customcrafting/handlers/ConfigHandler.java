package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.ItemConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigHandler {

    private Plugin instance;
    private WolfyUtilities api;
    private ConfigAPI configAPI;
    private LanguageAPI languageAPI;

    private MainConfig mainConfig;


    public ConfigHandler(WolfyUtilities api){
        this.api = api;
        this.instance = api.getPlugin();
        this.configAPI = api.getConfigAPI();
        this.languageAPI = api.getLanguageAPI();
    }

    public void load(){
        File recipes = new File(instance.getDataFolder(), "recipes");
        if (!instance.getDataFolder().exists() || !recipes.exists()) {
            ItemConfig itemConfig = new ItemConfig(api.getConfigAPI(), "defaults/workbench_item", "customcrafting", "workbench");
            ItemConfig itemConfig2 = new ItemConfig(api.getConfigAPI(), "defaults/furnace_item", "customcrafting", "furnace");
            ItemConfig itemConfig3 = new ItemConfig(api.getConfigAPI(), "defaults/compressed_cobble", "customcrafting", "compressed_cobblestone");
            CustomConfig config = new CraftConfig(api.getConfigAPI(), "defaults/workbench_craft", "customcrafting", "workbench");
            CustomConfig config2 = new CraftConfig(api.getConfigAPI(), "defaults/furnace_recipe", "customcrafting", "furnace");
        }
        this.mainConfig = new MainConfig(configAPI);
        loadLang();
    }

    public void loadLang(){
        String chosenLang = configAPI.getConfig("config").getString("language");
        Config langConf = new Config(configAPI, "me/wolfyscript/customcrafting/configs/lang", instance.getDataFolder().getPath()+"/lang", chosenLang, true);
        langConf.loadDefaults();
        System.out.println("Loading language \""+chosenLang+"\" v"+ langConf.getString("version") +" translated by "+langConf.getString("author"));
        languageAPI.registerLanguage(new Language(chosenLang, langConf, configAPI));
    }

    public MainConfig getConfig() {
        return mainConfig;
    }
}
