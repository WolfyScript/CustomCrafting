package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KnowledgeBook {

    private int page;
    private Setting setting;
    private String recipeID;

    private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private CustomItem result;

    private CustomRecipe customRecipe;

    private int timerTask;
    private HashMap<Integer, Integer> timerTimings;

    public KnowledgeBook(){
        this.page = 0;
        this.setting = Setting.MAIN_MENU;
        this.recipeID = "";
        this.customRecipe = null;
        this.timerTask = -1;
        this.timerTimings = new HashMap<>();
    }

    public HashMap<Integer, Integer> getTimerTimings() {
        return timerTimings;
    }

    public void setTimerTimings(HashMap<Integer, Integer> timerTimings) {
        this.timerTimings = timerTimings;
    }

    public void setTimerTask(int task){
        this.timerTask = task;
    }

    public int getTimerTask() {
        return timerTask;
    }

    public void stopTimerTask(){
        if(timerTask != -1){
            Bukkit.getScheduler().cancelTask(timerTask);
            timerTask = -1;
            timerTimings = new HashMap<>();
        }
    }

    public CustomRecipe getCustomRecipe(){
        return customRecipe;
    }

    public void setCustomRecipe(CustomRecipe customRecipe) {
        this.customRecipe = customRecipe;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public HashMap<Character, ArrayList<CustomItem>> getIngredients() {
        return new HashMap<>();
    }

    public CustomItem getResult() {
        return result;
    }

    public void setResult(CustomItem result) {
        this.result = result;
    }
}
