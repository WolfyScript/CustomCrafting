package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import org.bukkit.plugin.Plugin;

import java.io.*;

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
        this.mainConfig = new MainConfig(configAPI);
        loadLang();
    }

    public void loadLang(){
        String chosenlang = configAPI.getConfig("config").getString("language");
        languageAPI.registerLanguage(new Language(chosenlang, new Config(configAPI, "me/wolfyscript/customcrafting/configs/lang", instance.getDataFolder().getPath()+"/lang", chosenlang), configAPI));
    }

    public MainConfig getConfig() {
        return mainConfig;
    }
}
