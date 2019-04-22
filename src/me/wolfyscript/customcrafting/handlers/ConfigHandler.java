package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.items.ItemConfig;
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
            ItemConfig itemConfig3 = new ItemConfig(api.getConfigAPI(), "defaults/compressed_cobble", "customcrafting", "compressed_cobblestone");
            CustomConfig config = new CraftConfig(api.getConfigAPI(), "defaults/workbench_craft", "customcrafting", "workbench");
        }
        this.mainConfig = new MainConfig(configAPI);
        loadLang();
    }

    public void loadLang(){
        String chosenLang = configAPI.getConfig("main_config").getString("language");
        Config langConf;
        if(CustomCrafting.getInst().getResource("me/wolfyscript/customcrafting/configs/lang" + "/" + chosenLang + ".yml") != null){
            langConf = new Config(configAPI, "me/wolfyscript/customcrafting/configs/lang", instance.getDataFolder().getPath()+"/lang", chosenLang, true);
        }else{
            langConf = new Config(configAPI, "me/wolfyscript/customcrafting/configs/lang", "en_US", instance.getDataFolder().getPath()+"/lang", chosenLang, false);
        }
        langConf.loadDefaults();
        System.out.println("Loading language \""+chosenLang+"\" v"+ langConf.getString("version") +" translated by "+langConf.getString("author"));

        languageAPI.registerLanguage(new Language(chosenLang, langConf, configAPI));
    }

    public MainConfig getConfig() {
        return mainConfig;
    }
}
