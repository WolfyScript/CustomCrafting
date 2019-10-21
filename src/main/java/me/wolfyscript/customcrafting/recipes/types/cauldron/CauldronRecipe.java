package me.wolfyscript.customcrafting.recipes.types.cauldron;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.entity.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CauldronRecipe implements CustomRecipe<CauldronConfig> {

    private boolean exactMeta;
    private String group;
    private Conditions conditions;

    private int cookingTime;
    private int waterLevel;
    private RecipePriority priority;
    private float xp;
    private List<CustomItem> result;
    private List<CustomItem> ingredients;
    private boolean dropItems;
    private boolean needsFire;
    private boolean noWater;

    private String id;
    private CauldronConfig config;

    public CauldronRecipe(CauldronConfig config){
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
        this.noWater = config.isNoWater();
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

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public boolean needsFire() {
        return needsFire;
    }

    public void setNeedsFire(boolean needsFire) {
        this.needsFire = needsFire;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public boolean isNoWater() {
        return noWater;
    }

    public void setNoWater(boolean noWater) {
        this.noWater = noWater;
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

    public List<Item> checkRecipe(List<Item> items){
        List<Item> validItems = new ArrayList<>();
        for(CustomItem customItem : getIngredients()){
            for(Item item : items){
                if(customItem.isSimilar(item.getItemStack(), isExactMeta()) && customItem.getAmount() == item.getItemStack().getAmount()){
                    validItems.add(item);
                    break;
                }
            }
        }
        if(validItems.size() >= ingredients.size()){
            return validItems;
        }
        return null;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }
}
