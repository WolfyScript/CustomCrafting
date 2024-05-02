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
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.lib.com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.language.LanguageAPI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
        try {
            loadRecipeBookConfig();
        } catch (IOException e) {
            customCrafting.getLogger().severe("Failed to load recipe_book.conf");
            e.printStackTrace();
            this.recipeBookConfig = new RecipeBookConfig(false);
        }
        renameOldRecipesFolder();
        try {
            loadDefaults();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadRecipeBookConfig() throws IOException {
        var recipeBookFileJson = new File(customCrafting.getDataFolder(), "recipe_book.json");
        var recipeBookFile = new File(customCrafting.getDataFolder(), "recipe_book.conf");
        if (!recipeBookFileJson.exists() && !recipeBookFile.exists()) {
            customCrafting.saveResource("recipe_book.conf", true);
        } else if (recipeBookFileJson.exists() && !recipeBookFile.exists()) {
            // The old json file is used and there is no hocon file available, so let's rename it.
            Files.move(recipeBookFileJson.toPath(), recipeBookFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        }
        // At this point both json and hocon file might be present (due to previous conversion logic), so just load the hocon variant.
        this.recipeBookConfig = customCrafting.getApi().getJacksonMapperUtil().getGlobalMapper().readValue(recipeBookFile, RecipeBookConfig.class);
    }

    public void renameOldRecipesFolder() {
        if (!DataHandler.DATA_FOLDER.exists()) { //Check for the old recipes folder and rename it to the new data folder.
            var old = new File(customCrafting.getDataFolder() + File.separator + "recipes");
            if (old.exists() && !old.renameTo(DataHandler.DATA_FOLDER)) {
                customCrafting.getLogger().severe("Couldn't rename folder to the new required names!");
            }
        }
    }

    public void loadDefaults() throws IOException {
        if (mainConfig.resetRecipeBook()) {
            saveDefault("recipe_book");
        }
        if (mainConfig.resetAdvancedWorkbench()) {
            saveDefault("advanced_crafting_table");
        }
    }

    private void saveDefault(String file) throws IOException {
        String itemPath = "data/customcrafting/items/";
        String recipePath = "data/customcrafting/recipes/";

        File jsonFileItem = new File(customCrafting.getDataFolder(), itemPath + file + ".json");
        if (jsonFileItem.exists()) {
            if (!jsonFileItem.renameTo(new File(customCrafting.getDataFolder(), itemPath + file + ".conf"))) {
                Files.delete(jsonFileItem.toPath());
            }
        }
        File jsonFileRecipe = new File(customCrafting.getDataFolder(), recipePath + file + ".json");
        if (jsonFileRecipe.exists()) {
            if (!jsonFileRecipe.renameTo(new File(customCrafting.getDataFolder(), recipePath + file + ".conf"))) {
                Files.delete(jsonFileRecipe.toPath());
            }
        }

        customCrafting.saveResource(itemPath + file + ".conf", true);
        customCrafting.saveResource(recipePath + file + ".conf", true);
    }

    public void loadLang() {
        var chosenLang = mainConfig.getString("language");
        //Export all the available languages
        customCrafting.saveResource("lang/en_US.json", true);
        customCrafting.saveResource("lang/de_DE.json", true);
        customCrafting.saveResource("lang/zh_CN.json", true);
        customCrafting.saveResource("lang/ru_RU.json", true);
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
        getConfig().save();
    }

    public MainConfig getConfig() {
        return mainConfig;
    }

    public RecipeBookConfig getRecipeBookConfig() {
        return recipeBookConfig;
    }

    public void saveNewRecipeBookConfig(RecipeBookConfig editorCopy, GuiWindow<CCCache> window, GuiHandler<CCCache> guiHandler) {
        try {
            File recipeBookFile = new File(customCrafting.getDataFolder(), "recipe_book.conf");
            if (!recipeBookFile.renameTo(new File(customCrafting.getDataFolder(), "recipe_book_backup.conf"))) {
                window.sendMessage(guiHandler, window.getCluster().translatedMsgKey("save.failed_backup"));
            }
            customCrafting.getApi().getJacksonMapperUtil().getGlobalMapper().writer(customCrafting.getConfigHandler().getConfig().isPrettyPrinting() ? new DefaultPrettyPrinter() : null)
                    .writeValue(recipeBookFile, editorCopy);
            recipeBookConfig = editorCopy;
            window.sendMessage(guiHandler, window.getCluster().translatedMsgKey("save.success"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
