package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.data.cache.Furnace;
import me.wolfyscript.customcrafting.data.cache.Items;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerCache {

    private UUID uuid;

    private HashMap<String, Object> CACHE = new HashMap<>();

    private Items items = new Items();

    //LIST OF ALL RECIPE CACHES
    private Workbench workbench =  new Workbench();
    private Furnace furnace = new Furnace();

    public PlayerCache(UUID uuid) {
        this.uuid = uuid;
        setSetting(Setting.MAIN_MENU);

        setSubSetting("");

        CACHE.put("stats", new HashMap<String, Object>());
        setAmountCrafted(0);
        setAmountAvancedCrafted(0);
        setAmountNormalCrafted(0);

    }

    public PlayerCache(UUID uuid, HashMap<String, Object> stats){
        this(uuid);
        setStats(stats);
    }

    public UUID getUuid() {
        return uuid;
    }

    public HashMap<String, Object> getCACHE() {
        return CACHE;
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

    public void setFurnace(Furnace furnace) {
        this.furnace = furnace;
    }

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    private Object getObject(String key, String subKey) {
        return ((HashMap<String, Object>) CACHE.getOrDefault(key, new HashMap<String, Object>())).get(subKey);
    }

    private void setObject(String key, String subKey, Object object) {
        HashMap<String, Object> map = (HashMap<String, Object>) CACHE.get(key);
        map.put(subKey, object);
        CACHE.put(key, map);
    }

    private Object getObject(String key) {
        return CACHE.get(key);
    }

    private void setObject(String key, Object object) {
        CACHE.put(key, object);
    }

    //Player Stats
    public HashMap<String, Object> getStats(){
        return (HashMap<String, Object>) getObject("stats");
    }

    public void setStats(HashMap<String, Object> stats){
        setObject("stats",stats);
    }

    public void addAmountCrafted(int amount){
        setAmountCrafted(getAmountCrafted()+amount);
    }

    public void setAmountCrafted(int amount){
        setObject("stats", "amount_crafted", amount);
    }

    public int getAmountCrafted(){
        return (int) getObject("stats","amount_crafted");
    }

    public void addAmountAdvancedCrafted(int amount){
        setAmountAvancedCrafted(getAmountAdvancedCrafted()+amount);
    }

    public void setAmountAvancedCrafted(int amount){
        setObject("stats", "amount_advanced_crafted", amount);
    }

    public int getAmountAdvancedCrafted(){
        return (int) getObject("stats","amount_advanced_crafted");
    }

    public void addAmountNormalCrafted(int amount){
        setAmountNormalCrafted(getAmountNormalCrafted()+amount);
    }

    public void setAmountNormalCrafted(int amount){
        setObject("stats", "amount_normal_crafted", amount);
    }

    public int getAmountNormalCrafted(){
        return (int) getObject("stats","amount_normal_crafted");
    }


    //Main Settings
    public Setting getSetting() {
        return (Setting) getObject("setting");
    }

    public void setSetting(Setting setting) {
        setObject("setting", setting);
    }

    //Sub-Settings for GUIs
    public String getSubSetting() {
        return (String) getObject("sub_setting");
    }

    public void setSubSetting(String setting) {
        setObject("sub_setting", setting);
    }

}
