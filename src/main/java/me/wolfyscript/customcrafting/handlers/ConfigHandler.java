/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBookConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHandler {

    private final CustomCrafting customCrafting;
    private final LanguageAPI languageAPI;
    private final MainConfig mainConfig;
    private RecipeBookConfig recipeBookConfig;

    public ConfigHandler(CustomCrafting customCrafting) {
        WolfyUtilities api = customCrafting.getApi();
        this.customCrafting = customCrafting;
        var configAPI = api.getConfigAPI();
        this.languageAPI = api.getLanguageAPI();
        var oldConfigFile = new File(customCrafting.getDataFolder().getPath(), "main_config.yml");//Makes sure that if a config with the old name already exists, it's renamed to the new config name.
        if (oldConfigFile.exists() && !oldConfigFile.renameTo(new File(customCrafting.getDataFolder().getPath(), "config.yml"))) {
            customCrafting.getLogger().severe("Couldn't rename 'main_config.yml' to 'config.yml'!");
        }
        this.mainConfig = new MainConfig(configAPI, customCrafting);
        mainConfig.loadDefaults();
        configAPI.registerConfig(mainConfig);
        api.getConfigAPI().setPrettyPrinting(mainConfig.isPrettyPrinting());
    }

    public void load() {
        if (mainConfig != null) {
            mainConfig.load();
        }
        loadLang();
        loadRecipeBookConfig();
        renameOldRecipesFolder();
        loadDefaults();
    }

    public void loadRecipeBookConfig() {
        var recipeBookFile = new File(customCrafting.getDataFolder(), "recipe_book.json");
        if (!recipeBookFile.exists()) {
            customCrafting.saveResource("recipe_book.json", true);
        }
        try {
            this.recipeBookConfig = JacksonUtil.getObjectMapper().readValue(recipeBookFile, RecipeBookConfig.class);
        } catch (IOException e) {
            customCrafting.getLogger().severe("Failed to load recipe_book.json");
            e.printStackTrace();
            this.recipeBookConfig = new RecipeBookConfig();
        }
    }

    public void renameOldRecipesFolder() {
        if (!DataHandler.DATA_FOLDER.exists()) { //Check for the old recipes folder and rename it to the new data folder.
            var old = new File(customCrafting.getDataFolder() + File.separator + "recipes");
            if (old.exists() && !old.renameTo(DataHandler.DATA_FOLDER)) {
                customCrafting.getLogger().severe("Couldn't rename folder to the new required names!");
            }
        }
    }

    public void loadDefaults() {
        if (mainConfig.resetRecipeBook()) {
            customCrafting.saveResource("data/customcrafting/items/recipe_book.json", true);
            customCrafting.saveResource("data/customcrafting/recipes/recipe_book.json", true);
        }
        if (mainConfig.resetAdvancedWorkbench()) {
            customCrafting.saveResource("data/customcrafting/items/advanced_crafting_table.json", true);
            customCrafting.saveResource("data/customcrafting/recipes/advanced_crafting_table.json", true);
        }
    }

    public void loadLang() {
        var chosenLang = mainConfig.getString("language");
        //Export all the available languages
        customCrafting.saveResource("lang/en_US.json", true);
        customCrafting.saveResource("lang/de_DE.json", true);
        customCrafting.saveResource("lang/zh_CN.json", true);
        //The default language to use and to which it falls back if a key is not found in the active language
        var fallBackLanguage = languageAPI.loadLangFile("en_US");
        languageAPI.registerLanguage(fallBackLanguage);
        customCrafting.getLogger().info(() -> "Loaded fallback language \"en_US\" v" + fallBackLanguage.getVersion() + " translated by " + String.join(", ", fallBackLanguage.getAuthors()));
        //Load the chosen language
        if (Files.exists(Path.of(customCrafting.getDataFolder().getPath(), "lang", chosenLang + ".json"))) {
            var language = languageAPI.loadLangFile(chosenLang);
            languageAPI.registerLanguage(language);
            languageAPI.setActiveLanguage(language);
            customCrafting.getLogger().info(() -> "Loaded active language \"" + chosenLang + "\" v" + language.getVersion() + " translated by " + String.join(", ", language.getAuthors()));
        }
    }

    public void save() throws IOException {
        if (this.recipeBookConfig != null) {
            JacksonUtil.getObjectWriter(getConfig().isPrettyPrinting()).writeValue(new File(customCrafting.getDataFolder(), "recipe_book.json"), this.recipeBookConfig);
        }
        getConfig().save();
    }

    public MainConfig getConfig() {
        return mainConfig;
    }

    public RecipeBookConfig getRecipeBookConfig() {
        return recipeBookConfig;
    }
}
