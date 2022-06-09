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

package me.wolfyscript.customcrafting.configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.YamlConfiguration;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainConfig extends YamlConfiguration {

    public MainConfig(ConfigAPI configAPI, CustomCrafting customCrafting) {
        super(configAPI, customCrafting.getDataFolder().getPath(), "config", "", "config", false);
    }

    @Override
    public void init() {
    }

    public boolean isGUIDrawBackground() {
        return getBoolean("gui.draw_background", true);
    }

    public void setGUIDrawBackground(boolean drawBackground) {
        set("gui.draw_background", drawBackground);
    }

    public List<String> getCustomCraftingAlias() {
        return getStringList("commands.alias");
    }

    public String getLanguage() {
        return getString("language");
    }

    public void setLanguage(String lang) {
        set("language", lang);
    }

    public int getDataVersion() {
        return getInt("data.version");
    }

    public void setDataVersion(int version) {
        set("data.version", version);
    }

    public boolean updateOldCustomItems() {
        return getBoolean("custom_items.update", true);
    }

    public boolean isResetCreatorAfterSave() {
        return getBoolean("creator.reset_after_save");
    }

    public void setResetCreatorAfterSave(boolean reset) {
        set("creator.reset_after_save", reset);
    }

    public boolean resetRecipeBook() {
        return getBoolean("recipe_book.reset");
    }

    public void setResetRecipeBook(boolean reset) {
        set("recipe_book.reset", reset);
    }

    public boolean isRecipeBookKeepLastOpen() {
        return getBoolean("recipe_book.keep_last_open");
    }

    public void setRecipeBookKeepLastOpen(boolean keepLastOpen) {
        set("recipe_book.keep_last_open", keepLastOpen);
    }

    public boolean isAdvancedWorkbenchEnabled() {
        return getBoolean("crafting_table.enable");
    }

    public void setAdvancedWorkbenchEnabled(boolean enabled) {
        set("crafting_table.enable", enabled);
    }

    public boolean resetAdvancedWorkbench() {
        return getBoolean("crafting_table.reset");
    }

    public void setResetAdvancedWorkbench(boolean reset) {
        set("crafting_table.reset", reset);
    }

    public long getAutosaveInterval() {
        return getLong("data.auto_save.interval");
    }

    public boolean isAutoSaveMessage() {
        return getBoolean("data.auto_save.message");
    }

    public boolean isPrintingStacktrace() {
        return getBoolean("data.print_stacktrace");
    }

    public Set<String> getDisabledRecipes() {
        return new HashSet<>(getStringList("recipes.disabled_recipes"));
    }

    public void setDisabledRecipes(Set<NamespacedKey> recipes) {
        set("recipes.disabled_recipes", recipes.parallelStream().map(NamespacedKey::toString).toList());
    }

    public boolean isPrettyPrinting() {
        return getBoolean("recipes.pretty_printing");
    }

    public void setPrettyPrinting(boolean prettyPrinting) {
        set("recipes.pretty_printing", prettyPrinting);
    }

    public boolean isLockedDown() {
        return getBoolean("recipes.lockdown");
    }

    public void toggleLockDown() {
        setLockDown(!isLockedDown());
    }

    public void setLockDown(boolean lockdown) {
        set("recipes.lockdown", lockdown);
    }

    public ConfigurationSection getDatabaseSettings() {
        return getConfigurationSection("database");
    }

    public ConfigurationSection getLocalStorageSettings() {
        return getConfigurationSection("local_storage");
    }

    public boolean isLocalStorageEnabled() {
        return getBoolean("local_storage.load");
    }

    public boolean isLocalStorageBeforeDatabase() {
        return getBoolean("local_storage.before_database");
    }

    public boolean isDataOverride() {
        return getBoolean("local_storage.override", false);
    }

    public boolean isDatabaseEnabled() {
        return getBoolean("database.enabled");
    }

    public String getDatabaseHost() {
        return getString("database.host");
    }

    public int getDatabasePort() {
        return getInt("database.port");
    }

    public String getDatabaseSchema() {
        return getString("database.schema");
    }

    public String getDatabaseUsername() {
        return getString("database.username");
    }

    public String getDatabasePassword() {
        return getString("database.password");
    }

    public boolean isBrewingRecipes() {
        return getBoolean("recipes.brewing");
    }

}
