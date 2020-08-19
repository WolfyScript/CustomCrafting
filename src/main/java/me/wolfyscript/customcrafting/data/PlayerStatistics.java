package me.wolfyscript.customcrafting.data;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStatistics {

    private final UUID uuid;

    private HashMap<String, Object> CACHE = new HashMap<>();

    public PlayerStatistics(UUID uuid) {
        this.uuid = uuid;
        setAmountCrafted(0);
        setAmountAdvancedCrafted(0);
        setAmountNormalCrafted(0);
    }

    public PlayerStatistics(UUID uuid, HashMap<String, Object> stats) {
        this(uuid);
        setStats(stats);
    }

    public UUID getUuid() {
        return uuid;
    }

    //Player Stats
    //Main Settings

    private Object getObjectOrDefault(String key, Object defaultValue) {
        return CACHE.getOrDefault(key, defaultValue);
    }

    private void setObject(String key, Object object) {
        CACHE.put(key, object);
    }

    public HashMap<String, Object> getStats() {
        return CACHE;
    }

    public void setStats(HashMap<String, Object> stats) {
        CACHE = stats;
    }

    public void setDarkMode(boolean darkMode) {
        CACHE.put("dark_mode", darkMode);
    }

    public boolean getDarkMode() {
        return (boolean) CACHE.getOrDefault("dark_mode", false);
    }

    public void addAmountCrafted(int amount) {
        setAmountCrafted(getAmountCrafted() + amount);
    }

    public void setAmountCrafted(int amount) {
        setObject("amount_crafted", amount);
    }

    public int getAmountCrafted() {
        return (int) getObjectOrDefault("amount_crafted", 0);
    }

    public void addAmountAdvancedCrafted(int amount) {
        setAmountAdvancedCrafted(getAmountAdvancedCrafted() + amount);
    }

    public void setAmountAdvancedCrafted(int amount) {
        setObject("amount_advanced_crafted", amount);
    }

    public int getAmountAdvancedCrafted() {
        return (int) getObjectOrDefault("amount_advanced_crafted", 0);
    }

    public void addAmountNormalCrafted(int amount) {
        setAmountNormalCrafted(getAmountNormalCrafted() + amount);
    }

    public void setAmountNormalCrafted(int amount) {
        setObject("amount_normal_crafted", amount);
    }

    public int getAmountNormalCrafted() {
        return (int) getObjectOrDefault("amount_normal_crafted", 0);
    }

    public HashMap<String, Integer> getRecipeCrafts() {
        return (HashMap<String, Integer>) getObjectOrDefault("recipe_crafts", new HashMap<String, Integer>());
    }

    public void addRecipeCrafts(String key) {
        setRecipeCrafts(key, getRecipeCrafts(key) + 1);
    }

    public void setRecipeCrafts(String key, int amount) {
        HashMap<String, Integer> recipeCrafts = getRecipeCrafts();
        recipeCrafts.put(key, amount);
        setObject("recipe_crafts", recipeCrafts);
    }

    public int getRecipeCrafts(String key) {
        HashMap<String, Integer> recipeCrafts = getRecipeCrafts();
        return recipeCrafts.getOrDefault(key, 0);
    }
}
