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

import java.util.HashMap;
import java.util.Map;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.YamlConfiguration;
import me.wolfyscript.utilities.util.NamespacedKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

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

    public boolean isNMSBasedCrafting() {
        return getBoolean("recipes.nms_based_crafting", false);
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

    public LocalStorageSettings getLocalStorageSettings() {
        //return getObject("local_storage", LocalStorageSettings.class);
        return (LocalStorageSettings) ConfigurationSerialization.deserializeObject(getObject("local_storage", Map.class, new HashMap<>()), LocalStorageSettings.class);
    }

    public DatabaseSettings getDatabaseSettings() {
        return (DatabaseSettings) ConfigurationSerialization.deserializeObject(getObject("database", Map.class, new HashMap<>()), DatabaseSettings.class);
    }

    public boolean isBrewingRecipes() {
        return getBoolean("recipes.brewing");
    }

    public static class LocalStorageSettings implements ConfigurationSerializable {

        private static final String LOAD = "load";
        private static final String BEFORE_DATABASE = "before_database";
        private static final String OVERRIDE = "override";

        private final boolean load;
        private final boolean beforeDatabase;
        private final boolean override;

        public LocalStorageSettings(Map<String, Object> values) {
            this.load = values.get(LOAD) instanceof Boolean bool && bool;
            this.beforeDatabase = values.get(BEFORE_DATABASE) instanceof Boolean bool && bool;
            this.override = values.get(OVERRIDE) instanceof Boolean bool && bool;
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> result = new HashMap<>();
            result.put(LOAD, load);
            result.put(BEFORE_DATABASE, beforeDatabase);
            result.put(OVERRIDE, override);
            return result;
        }

        public boolean isEnabled() {
            return load;
        }

        public boolean isBeforeDatabase() {
            return beforeDatabase;
        }

        public boolean isOverride() {
            return override;
        }
    }


    public static class DatabaseSettings implements ConfigurationSerializable {

        private static final String ENABLED = "enabled";
        private static final String HOST = "host";
        private static final String PORT = "port";
        private static final String SCHEMA = "schema";
        private static final String USERNAME = "username";
        private static final String PASSWORD = "password";

        private final boolean enabled;
        private final String host;
        private final int port;
        private final String schema;
        private final String username;
        private final String password;

        public DatabaseSettings(Map<String, Object> values) {
            this.enabled = values.get(ENABLED) instanceof Boolean bool && bool;
            this.host = (String) values.getOrDefault(HOST, "localhost");
            Object portVal = values.get(PORT);
            this.port = portVal instanceof Number ? NumberConversions.toInt(portVal) : 3306;
            this.schema = values.getOrDefault(SCHEMA, "mc_plugins").toString();
            this.username = values.getOrDefault(USERNAME, "minecraft").toString();
            this.password = values.getOrDefault(PASSWORD, "").toString();
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> result = new HashMap<>();
            result.put(ENABLED, enabled);
            result.put(HOST, host);
            result.put(PORT, port);
            result.put(SCHEMA, schema);
            result.put(USERNAME, username);
            result.put(PASSWORD, password);
            return result;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getSchema() {
            return schema;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

}
