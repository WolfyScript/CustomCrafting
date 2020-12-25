package me.wolfyscript.customcrafting.configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.YamlConfiguration;

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

    public boolean isCCenabled() {
        return getBoolean("commands.cc");
    }

    public String getLanguage() {
        return getString("language");
    }

    public void setlanguage(String lang) {
        set("language", lang);
    }

    public boolean isResetCreatorAfterSave() {
        return getBoolean("creator.reset_after_save");
    }

    public void setResetCreatorAfterSave(boolean reset) {
        set("creator.reset_after_save", reset);
    }

    public boolean resetKnowledgeBook() {
        return getBoolean("knowledgebook.reset");
    }

    public void setResetKnowledgeBook(boolean reset) {
        set("knowledgebook.reset", reset);
    }

    public void setAdvancedWorkbenchEnabled(boolean enabled) {
        set("workbench.enable", enabled);
    }

    public boolean isAdvancedWorkbenchEnabled() {
        return getBoolean("workbench.enable");
    }

    public boolean resetAdvancedWorkbench() {
        return getBoolean("workbench.reset");
    }

    public void setResetAdvancedWorkbench(boolean reset) {
        set("workbench.reset", reset);
    }

    public int getAutosaveInterval() {
        return getInt("data.auto_save.interval");
    }

    public boolean isAutoSaveMesage() {
        return getBoolean("data.auto_save.message");
    }

    public List<String> getDisabledRecipes() {
        return getStringList("recipes.disabled_recipes");
    }

    public void setDisabledrecipes(List<String> recipes) {
        set("recipes.disabled_recipes", recipes);
    }

    public boolean isPrettyPrinting() {
        return getBoolean("recipes.pretty_printing");
    }

    public void setPrettyPrinting(boolean prettyPrinting) {
        set("recipes.pretty_printing", prettyPrinting);
    }

    public List<String> getCommandsSuccessCrafted() {
        return getStringList("workbench.commands.successful_craft");
    }

    public List<String> getCommandsDeniedCraft() {
        return getStringList("workbench.commands.denied_craft");
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

    public boolean isDatabankEnabled() {
        return getBoolean("databank.enabled");
    }

    public String getDatabankHost() {
        return getString("databank.host");
    }

    public int getDatabankPort() {
        return getInt("databank.port");
    }

    public String getDatabankDataBase() {
        return getString("databank.database");
    }

    public String getDatabankUsername() {
        return getString("databank.username");
    }

    public String getDataBankPassword() {
        return getString("databank.password");
    }

}
