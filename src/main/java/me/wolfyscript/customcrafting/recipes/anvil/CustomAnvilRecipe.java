package me.wolfyscript.customcrafting.recipes.anvil;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.inventory.ItemStack;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CustomAnvilRecipe implements CustomRecipe {

    private boolean permission;
    private boolean exactMeta;
    private boolean blockRepair;
    private boolean blockRename;
    private boolean blockEnchant;
    private RecipePriority priority;

    private String id;
    private AnvilConfig config;

    private Mode mode;
    private int repairCost;
    private boolean applyRepairCost;
    private RepairCostMode repairCostMode;
    private CustomItem result;
    private int durability;

    private List<CustomItem> inputLeft;
    private List<CustomItem> inputRight;

    public CustomAnvilRecipe(AnvilConfig config){
        this.config = config;
        this.permission = config.needPerm();
        this.exactMeta = config.isExactMeta();
        this.blockEnchant = config.isBlockEnchant();
        this.blockRename = config.isBlockRename();
        this.blockRepair = config.isBlockRepairing();
        this.priority = config.getPriority();

        this.id = config.getId();

        this.repairCost = config.getRepairCost();
        this.mode = config.getMode();
        this.applyRepairCost = config.isApplyRepairCost();
        this.repairCostMode = config.getRepairCostMode();
        this.durability = 0;
        this.result = null;
        if(config.getMode().equals(Mode.DURABILITY)){
            this.durability = config.getDurability();
        }else if(config.getMode().equals(Mode.RESULT)){
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

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
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

    public List<CustomItem> getInputLeft() {
        return inputLeft;
    }

    public void setInputLeft(List<CustomItem> inputLeft) {
        this.inputLeft = inputLeft;
    }

    public List<CustomItem> getInputRight() {
        return inputRight;
    }

    public boolean hasInputLeft(){
        return !getInputLeft().isEmpty();
    }

    public boolean hasInputRight(){
        return !getInputRight().isEmpty();
    }

    public void setInputRight(List<CustomItem> inputRight) {
        this.inputRight = inputRight;
    }

    public boolean isBlockRepair() {
        return blockRepair;
    }

    public void setBlockRepair(boolean blockRepair) {
        this.blockRepair = blockRepair;
    }

    public boolean isBlockRename() {
        return blockRename;
    }

    public void setBlockRename(boolean blockRename) {
        this.blockRename = blockRename;
    }

    public boolean isBlockEnchant() {
        return blockEnchant;
    }

    public void setBlockEnchant(boolean blockEnchant) {
        this.blockEnchant = blockEnchant;
    }

    public boolean isApplyRepairCost() {
        return applyRepairCost;
    }

    public void setApplyRepairCost(boolean applyRepairCost) {
        this.applyRepairCost = applyRepairCost;
    }

    public RepairCostMode getRepairCostMode() {
        return repairCostMode;
    }

    public void setRepairCostMode(RepairCostMode repairCostMode) {
        this.repairCostMode = repairCostMode;
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

    public enum Mode{
        DURABILITY(1), RESULT(2), NONE(0);

        private int id;

        Mode(int id){
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Mode getById(int id){
            for(Mode mode : Mode.values()){
                if(mode.getId()==id)
                    return mode;
            }
            return NONE;
        }
    }

    public enum RepairCostMode{
        ADD(), MULTIPLY(), NONE();

        private static List<RepairCostMode> modes = new ArrayList<>();

        public static List<RepairCostMode> getModes() {
            if(modes.isEmpty()){
                modes.addAll(Arrays.asList(values()));
            }
            return modes;
        }
    }
}
