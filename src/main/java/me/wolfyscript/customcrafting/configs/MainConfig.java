package me.wolfyscript.customcrafting.configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class MainConfig extends YamlConfiguration {

    public MainConfig(ConfigAPI configAPI, CustomCrafting customCrafting) {
        super(configAPI, customCrafting.getDataFolder().getPath(), "config", "me/wolfyscript/customcrafting/configs", "config", false);
    }

    @Override
    public void init() {
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

    public List<String> getDisabledRecipes() {
        return getStringList("recipes.disabled_recipes");
    }

    public void setDisabledRecipes(List<String> recipes) {
        set("recipes.disabled_recipes", recipes);
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

}
