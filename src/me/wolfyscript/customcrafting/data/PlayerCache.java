package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.data.cache.*;
import me.wolfyscript.customcrafting.gui.Setting;

import java.util.*;

public class PlayerCache {

    private UUID uuid;
    private Setting setting;
    private String subSetting;

    private HashMap<String, Object> CACHE = new HashMap<>();

    private Items items = new Items();

    private KnowledgeBook knowledgeBook = new KnowledgeBook();

    private ChatRecipeList chatRecipeList = new ChatRecipeList();

    //RECIPE_LIST OF ALL RECIPE CACHES
    private Workbench workbench =  new Workbench();
    private Furnace furnace = new Furnace();
    private BlastingFurnace blastingFurnace = new BlastingFurnace();
    private Smoker smoker = new Smoker();
    private Campfire campfire = new Campfire();
    private Stonecutter stonecutter = new Stonecutter();


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

    public void resetWorkbench(){
        this.workbench = new Workbench();
    }

    public Furnace getFurnace() {
        return furnace;
    }

    public void resetCookingData(){
        switch (getSetting()){
            case BLAST_FURNACE:
                this.blastingFurnace = new BlastingFurnace();
            case SMOKER:
                this.smoker = new Smoker();
            case CAMPFIRE:
                this.campfire = new Campfire();
            case FURNACE_RECIPE:
                this.furnace = new Furnace();
        }
    }

    public void resetStonecutter(){
        this.stonecutter = new Stonecutter();
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

    public CookingData getCookingData(){
        switch (getSetting()){
            case BLAST_FURNACE:
                return getBlastingFurnace();
            case SMOKER:
                return getSmoker();
            case CAMPFIRE:
                return getCampfire();
            case FURNACE_RECIPE:
                return getFurnace();
        }
        return null;
    }

    public void setFurnace(Furnace furnace) {
        this.furnace = furnace;
    }

    public BlastingFurnace getBlastingFurnace() {
        return blastingFurnace;
    }

    public void setBlastingFurnace(BlastingFurnace blastingFurnace) {
        this.blastingFurnace = blastingFurnace;
    }

    public Smoker getSmoker() {
        return smoker;
    }

    public void setSmoker(Smoker smoker) {
        this.smoker = smoker;
    }

    public Campfire getCampfire() {
        return campfire;
    }

    public void setCampfire(Campfire campfire) {
        this.campfire = campfire;
    }

    public Stonecutter getStonecutter() {
        return stonecutter;
    }

    public void setStonecutter(Stonecutter stonecutter) {
        this.stonecutter = stonecutter;
    }

    public KnowledgeBook getKnowledgeBook() {
        return knowledgeBook;
    }

    public void setKnowledgeBook(KnowledgeBook knowledgeBook) {
        this.knowledgeBook = knowledgeBook;
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

    public void addRecipeCrafts(String key){
        setRecipeCrafts(key, getRecipeCrafts(key)+1);
    }

    public void setRecipeCrafts(String key, int amount){
        HashMap<String, Integer> recipeCrafts = getRecipeCrafts();
        recipeCrafts.put(key, amount);
        setObject("recipe_crafts", recipeCrafts);
    }

    public int getRecipeCrafts(String key){
        HashMap<String, Integer> recipeCrafts = getRecipeCrafts();
        return recipeCrafts.getOrDefault(key, 0);
    }

    public ChatRecipeList getChatRecipeList() {
        return chatRecipeList;
    }

    public void resetChatRecipeList() {
        this.chatRecipeList = new ChatRecipeList();
    }
}
