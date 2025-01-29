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
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.YamlConfiguration;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

public class MainConfig extends YamlConfiguration {

    public MainConfig(ConfigAPI configAPI, CustomCrafting customCrafting) {
        super(configAPI, customCrafting.getDataFolder().getPath(), "config", "", "config", false);
    }

    @Override
    public void init() {
    }

    @Override
    public void load() {
        super.load();
    }

    public String getRecipeBookTypeName(RecipeType<?> recipeType) {
        return getString("recipe_book.recipe_type_titles." + recipeType.getId());
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

    @Deprecated
    public int getDataVersion() {
        return getDataSettings().configVersion();
    }

    public void setDataVersion(int version) {
        getDataSettings().configVersion(version);
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

    @Deprecated
    public boolean isPrintingStacktrace() {
        return getDataSettings().printStackTrace();
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

    @Deprecated(forRemoval = true)
    public boolean isNMSBasedCrafting() {
        return false;
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

    public boolean isBrewingRecipes() {
        return getBoolean("recipes.brewing");
    }

    private <T> T getSetting(String key, Function<ConfigurationSection, T> creator) {
        ConfigurationSection section = getConfigurationSection(key);
        return section != null ? creator.apply(section) : null;
    }

    public DataSettings getDataSettings() {
        return getSetting("data", DataSettings::new);
    }

    public LocalStorageSettings getLocalStorageSettings() {
        return getSetting("local_storage", LocalStorageSettings::new);
    }

    public DatabaseSettings getDatabaseSettings() {
        return getSetting("database", DatabaseSettings::new);
    }

    public CookingSettings getFurnacesSettings() {
        return getSetting("cooking", CookingSettings::new);
    }

    public CauldronInteraction getCauldronInteraction() {
        return CauldronInteraction.valueOf(getString("workstation.cauldron.interaction", "NORMAL").toUpperCase(Locale.ROOT));
    }

    public void setCauldronInteraction(CauldronInteraction interaction) {
        set("workstation.cauldron.interaction", interaction.toString());
    }

    public enum CauldronInteraction {

        NORMAL,
        SNEAKING

    }

}
