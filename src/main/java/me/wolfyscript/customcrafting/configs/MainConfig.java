package me.wolfyscript.customcrafting.configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;

import java.util.List;

public class MainConfig extends Config {

    public MainConfig(ConfigAPI configAPI) {
        super(configAPI, CustomCrafting.getInst().getDataFolder().getPath(), "main_config", "me/wolfyscript/customcrafting/configs", "main_config", "yml", false);
    }

    @Override
    public void init() {
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

    public boolean workbenchFilter() {
        return getBoolean("knowledgebook.workbench_filter");
    }

    public void setWorkbenchFilter(boolean show) {
        set("knowledgebook.workbench_filter", show);
    }

    public boolean resetKnowledgeBookItem() {
        return getBoolean("knowledgebook.reset_item");
    }

    public void setResetKnowledgeBookItem(boolean reset) {
        set("knowledgebook.reset_item", reset);
    }

    public boolean resetKnowledgeBookRecipe() {
        return getBoolean("knowledgebook.reset_recipe");
    }

    public void setResetKnowledgeBookRecipe(boolean reset) {
        set("knowledgebook.reset_recipe", reset);
    }

    public void setAdvancedWorkbenchEnabled(boolean enabled) {
        set("workbench.enable", enabled);
    }

    public boolean isAdvancedWorkbenchEnabled() {
        return getBoolean("workbench.enable");
    }

    public boolean resetAdvancedWorkbenchItem() {
        return getBoolean("workbench.reset_item");
    }

    public void setResetAdvancedWorkbenchItem(boolean reset) {
        set("workbench.reset_item", reset);
    }

    public boolean resetAdvancedWorkbenchRecipe() {
        return getBoolean("workbench.reset_recipe");
    }

    public void setResetAdvancedWorkbenchRecipe(boolean reset) {
        set("workbench.reset_recipe", reset);
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

    public boolean displayContents() {
        return getBoolean("workbench.contents.display_items");
    }

    public List<String> getCommandsSuccessCrafted() {
        return getStringList("workbench.commands.successful_craft");
    }

    public List<String> getCommandsDeniedCraft() {
        return getStringList("workbench.commands.denied_craft");
    }

    public boolean isCCenabled() {
        return getBoolean("commands.cc");
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

    public boolean hideAds() {
        return getBoolean("secret.hide_ads");
    }

    public void setHideAds(boolean hideAds) {
        set("secret.hide_ads", hideAds);
    }

}
