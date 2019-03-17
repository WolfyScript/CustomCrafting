package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.data.cache.Furnace;
import me.wolfyscript.customcrafting.data.cache.Items;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.customcrafting.gui.Setting;

import java.util.*;

public class PlayerCache {

    private UUID uuid;
    private Setting setting;
    private String subSetting;

    private HashMap<String, Object> CACHE = new HashMap<>();

    private Items items = new Items();

    //LIST OF ALL RECIPE CACHES
    private Workbench workbench =  new Workbench();
    private Furnace furnace = new Furnace();

    public PlayerCache(UUID uuid) {
        this.uuid = uuid;
        this.setting = Setting.MAIN_MENU;
        this.subSetting = "";

        setAmountCrafted(0);
        setAmountAdvancedCrafted(0);
        setAmountNormalCrafted(0);

    }

    public PlayerCache(UUID uuid, HashMap<String, Object> stats){
        this(uuid);
        setStats(stats);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Workbench getWorkbench() {
        return workbench;
    }

    public void setWorkbench(Workbench workbench) {
        this.workbench = workbench;
    }

    public Furnace getFurnace() {
        return furnace;
    }

    //Player Stats
    //Main Settings
    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    //Sub-Settings for GUIs
    public String getSubSetting() {
        return subSetting;
    }

    public void setSubSetting(String setting) {
        this.subSetting = setting;
    }

    public void setFurnace(Furnace furnace) {
        this.furnace = furnace;
    }

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    private Object getObject(String key) {
        return CACHE.get(key);
    }

    private Object getObjectOrDefault(String key, Object defaultValue) {
        return CACHE.getOrDefault(key, defaultValue);
    }

    private void setObject(String key, Object object) {
        CACHE.put(key, object);
    }

    public HashMap<String, Object> getStats(){
        return CACHE;
    }

    public void setStats(HashMap<String, Object> stats){
        CACHE = stats;
    }

    public void addAmountCrafted(int amount){
        setAmountCrafted(getAmountCrafted()+amount);
    }

    public void setAmountCrafted(int amount){
        setObject("amount_crafted", amount);
    }

    public int getAmountCrafted(){
        return (int) getObjectOrDefault("amount_crafted", 0);
    }

    public void addAmountAdvancedCrafted(int amount){
        setAmountAdvancedCrafted(getAmountAdvancedCrafted()+amount);
    }

    public void setAmountAdvancedCrafted(int amount){
        setObject("amount_advanced_crafted", amount);
    }

    public int getAmountAdvancedCrafted(){
        return (int) getObjectOrDefault("amount_advanced_crafted", 0);
    }

    public void addAmountNormalCrafted(int amount){
        setAmountNormalCrafted(getAmountNormalCrafted()+amount);
    }

    public void setAmountNormalCrafted(int amount){
        setObject("amount_normal_crafted", amount);
    }

    public int getAmountNormalCrafted(){
        return (int) getObjectOrDefault("amount_normal_crafted", 0);
    }

    public HashMap<String, Integer> getRecipeCrafts(){
        return (HashMap<String, Integer>) getObjectOrDefault("recipe_crafts", new HashMap<String, Integer>());
    }

    public void setRecipeCrafts(String key, int amount){
        HashMap<String, Integer> recipeCrafts = (HashMap<String, Integer>) getObject("recipe_crafts");
        recipeCrafts.put(key, amount);
        setObject("recipe_crafts", recipeCrafts);
    }

    public int getRecipeCrafts(String key){
        HashMap<String, Integer> recipeCrafts = (HashMap<String, Integer>) getObject("recipe_crafts");
        return recipeCrafts.getOrDefault(key, 0);
    }

}
