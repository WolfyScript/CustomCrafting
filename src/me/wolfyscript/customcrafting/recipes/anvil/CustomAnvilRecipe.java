package me.wolfyscript.customcrafting.recipes.anvil;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;

public class CustomAnvilRecipe implements CustomRecipe {

    private boolean permission;
    private boolean exactMeta;
    private RecipePriority priority;

    private String id;
    private AnvilConfig config;

    private int mode;
    private int repairCost;
    private CustomItem result;
    private int durability;

    private HashMap<CustomItem, Boolean> inputLeft;
    private HashMap<CustomItem, Boolean> inputRight;

    public CustomAnvilRecipe(AnvilConfig config){
        this.config = config;
        this.permission = config.needPerm();
        this.exactMeta = config.isExactMeta();
        this.priority = config.getPriority();

        this.id = config.getId();

        this.repairCost = config.getRepairCost();
        this.mode = config.getMode();
        this.durability = 0;
        this.result = null;
        if(config.getMode() == 0){
            this.durability = config.getDurability();
        }else if(config.getMode() == 1){
            this.result = config.getResult();
        }

        this.inputLeft = config.getInputLeft();
        this.inputRight = config.getInputRight();
    }

    @Override
    public String getId() {
        return id;
    }

    public int getDurability() {
        return durability;
    }

    @Override
    @Nullable
    public CustomItem getCustomResult() {
        return result;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public void load() { }

    @Override
    public void save() { }

    @Override
    public CustomConfig getConfig() {
        return config;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    public void setPriority(RecipePriority priority) {
        this.priority = priority;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
    }

    public void setResult(CustomItem result) {
        this.result = result;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public HashMap<CustomItem, Boolean> getInputLeft() {
        return inputLeft;
    }

    public void setInputLeft(HashMap<CustomItem, Boolean> inputLeft) {
        this.inputLeft = inputLeft;
    }

    public HashMap<CustomItem, Boolean> getInputRight() {
        return inputRight;
    }

    public boolean hasInputLeft(){
        return !getInputLeft().isEmpty();
    }

    public boolean hasInputRight(){
        return !getInputRight().isEmpty();
    }

    public void setInputRight(HashMap<CustomItem, Boolean> inputRight) {
        this.inputRight = inputRight;
    }

    @Override
    @Deprecated
    public ItemStack getResult() {
        return result;
    }

    @Override
    @Deprecated
    public String getGroup() {
        return "";
    }
}
