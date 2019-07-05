package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.defaults.WorkbenchCraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.items.ItemConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        loadLang();

        ItemConfig itemConfig = new ItemConfig(api.getConfigAPI(),"me/wolfyscript/customcrafting/configs/custom_configs/defaults", "workbench_item", "customcrafting", "workbench", mainConfig.resetAdvancedWorkbenchItem());
        mainConfig.setResetAdvancedWorkbenchItem(false);

        CustomConfig config = new WorkbenchCraftConfig(api.getConfigAPI(), mainConfig.resetAdvancedWorkbenchRecipe());
        mainConfig.setResetAdvancedWorkbenchRecipe(false);
    }

    public void loadLang() {
        String chosenLang = configAPI.getConfig("main_config").getString("language");
        Config langConf;
        if (CustomCrafting.getInst().getResource("me/wolfyscript/customcrafting/configs/lang" + "/" + chosenLang + ".yml") != null) {
            langConf = new Config(configAPI, "me/wolfyscript/customcrafting/configs/lang", instance.getDataFolder().getPath() + "/lang", chosenLang, true);
        } else {
            langConf = new Config(configAPI, "me/wolfyscript/customcrafting/configs/lang", "en_US", instance.getDataFolder().getPath() + "/lang", chosenLang, false);
        }
        langConf.loadDefaults();
        System.out.println("Loading language \"" + chosenLang + "\" v" + langConf.getString("version") + " translated by " + langConf.getString("author"));

        languageAPI.registerLanguage(new Language(chosenLang, langConf, configAPI));
    }

    public MainConfig getConfig() {
        return mainConfig;
    }
}
