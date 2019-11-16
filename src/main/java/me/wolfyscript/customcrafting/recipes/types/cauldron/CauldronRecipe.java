package me.wolfyscript.customcrafting.recipes.types.cauldron;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class CauldronRecipe implements CustomRecipe<CauldronConfig> {

    private boolean exactMeta;
    private String group;
    private Conditions conditions;

    private int cookingTime;
    private int waterLevel;
    private RecipePriority priority;
    private float xp;
    private CustomItem handItem;
    private List<CustomItem> result;
    private List<CustomItem> ingredients;
    private boolean dropItems;
    private boolean needsFire;
    private boolean noWater;

    private String mythicMobName;
    private int mythicMobLevel;
    private Vector mythicMobMod;

    private String id;
    private CauldronConfig config;

    public CauldronRecipe(CauldronConfig config) {
        this.id = config.getId();
        this.config = config;
        this.result = config.getResult();
        this.ingredients = config.getIngredients();
        this.dropItems = config.dropItems();
        this.priority = config.getPriority();
        this.exactMeta = config.isExactMeta();
        this.group = config.getGroup();
        this.xp = config.getXP();
        this.needsFire = config.needsFire();
        this.conditions = config.getConditions();
        this.waterLevel = config.getWaterLevel();
        this.noWater = config.isWater();
        this.handItem = config.getHandItem();
        this.mythicMobLevel = config.getMythicMobLevel();
        this.mythicMobMod = config.getMythicMobMod();
        this.mythicMobName = config.getMythicMobName();
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public boolean needsFire() {
        return needsFire;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public boolean isNoWater() {
        return noWater;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public void load() {

    }

    @Override
    public CustomRecipe save(ConfigAPI configAPI, String namespace, String key) {
        return null;
    }

    @Override
    public CustomRecipe save(CauldronConfig config) {
        return null;
    }

    public void setPriority(RecipePriority priority) {
        this.priority = priority;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public List<CustomItem> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<CustomItem> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public CauldronConfig getConfig() {
        return config;
    }

    public void setConfig(CauldronConfig config) {
        this.config = config;
    }

    public boolean dropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public List<Item> checkRecipe(List<Item> items) {
        List<Item> validItems = new ArrayList<>();
        for (CustomItem customItem : getIngredients()) {
            for (Item item : items) {
                if (customItem.isSimilar(item.getItemStack(), isExactMeta()) && customItem.getAmount() == item.getItemStack().getAmount()) {
                    validItems.add(item);
                    break;
                }
            }
        }
        if (validItems.size() >= ingredients.size()) {
            return validItems;
        }
        return null;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    public CustomItem getHandItem() {
        return handItem;
    }

    public String getMythicMobName(){
        return mythicMobName;
    }

    public int getMythicMobLevel(){
        return mythicMobLevel;
    }

    public Vector getMythicMobMod(){
        return mythicMobMod;
    }
}
