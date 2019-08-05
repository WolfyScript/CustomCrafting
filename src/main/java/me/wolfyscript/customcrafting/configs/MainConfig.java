package me.wolfyscript.customcrafting.configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;

import java.util.List;

public class MainConfig extends Config {

    public MainConfig(ConfigAPI configAPI) {
        super(configAPI, CustomCrafting.getInst().getDataFolder().getPath(),"main_config", "me/wolfyscript/customcrafting/configs","main_config", "yml", false);
    }

    @Override
    public void init() {
    }

    public boolean isExperimentalFeatures() {
        return getBoolean("experimental_features");
    }

    public void setExperimentalFeatures(boolean experimentalFeatures){
        set("experimental_features", experimentalFeatures);
    }

    public boolean resetKnowledgeBookItem(){
        return getBoolean("knowledgebook.reset_item");
    }

    public void setResetKnowledgeBookItem(boolean reset){
        set("knowledgebook.reset_item", reset);
    }

    public boolean resetKnowledgeBookRecipe(){
        return getBoolean("knowledgebook.reset_recipe");
    }

    public void setResetKnowledgeBookRecipe(boolean reset){
        set("knowledgebook.reset_recipe", reset);
    }

    public boolean isAdvancedWorkbenchEnabled() {
        return getBoolean("workbench.enable");
    }

    public boolean resetAdvancedWorkbenchItem(){
        return getBoolean("workbench.reset_item");
    }

    public void setResetAdvancedWorkbenchItem(boolean reset){
        set("workbench.reset_item", reset);
    }

    public boolean resetAdvancedWorkbenchRecipe(){
        return getBoolean("workbench.reset_recipe");
    }

    public void setResetAdvancedWorkbenchRecipe(boolean reset){
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

    public boolean isPrettyPrinting(){
        return getBoolean("recipes.pretty_printing");
    }

    public void setPrettyPrinting(boolean prettyPrinting){
        set("recipes.pretty_printing", prettyPrinting);
    }

    public void setPreferredFileType(String fileType){
        set("recipes.preferred_file_type", fileType);
    }

    public String getPreferredFileType(){
        return getString("recipes.preferred_file_type");
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

    public boolean isLockedDown(){
        return getBoolean("recipes.lockdown");
    }

    public void toggleLockDown(){
        setLockDown(!isLockedDown());
    }

    public void setLockDown(boolean lockdown){
        set("recipes.lockdown", lockdown);
    }

    public boolean useVanillaKnowledgeBook(){
        return getBoolean("vanilla_knowledgebook");
    }

    public void useVanillaKnowledgeBook(boolean use){
        set("vanilla_knowledgebook", use);
    }

    public boolean isDatabankEnabled(){
        return getBoolean("databank.enabled");
    }

    public String getDatabankHost(){
        return getString("databank.host");
    }

    public int getDatabankPort(){
        return getInt("databank.port");
    }

    public String getDatabankDataBase(){
        return getString("databank.database");
    }

    public String getDatabankUsername(){
        return getString("databank.username");
    }

    public String getDataBankPassword(){
        return getString("databank.password");
    }



}
