package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Anvil extends RecipeData{

    private boolean exactMeta;
    private RecipePriority priority;

    private HashMap<Integer, List<CustomItem>> ingredients;

    //RESULT MODES
    private CustomAnvilRecipe.Mode mode;
    private int durability;

    private int repairCost;
    private boolean applyRepairCost;
    private CustomAnvilRecipe.RepairCostMode repairCostMode;
    private boolean blockRepair;
    private boolean blockRename;
    private boolean blockEnchant;

    public Anvil() {
        super();
        this.ingredients = new HashMap<>();
        this.ingredients.put(2, new ArrayList<>(Collections.singleton(new CustomItem(Material.AIR))));
        this.mode = CustomAnvilRecipe.Mode.RESULT;
        this.durability = 0;
        this.repairCost = 1;
        this.applyRepairCost = false;
        this.repairCostMode = CustomAnvilRecipe.RepairCostMode.NONE;
        this.blockEnchant = false;
        this.blockRename = false;
        this.blockRepair = false;
    }

    public boolean isExactMeta() {
        return exactMeta;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    public RecipePriority getPriority() {
        return priority;
    }

    public void setPriority(RecipePriority priority) {
        this.priority = priority;
    }

    public List<CustomItem> getResult() {
        return getIngredients(2);
    }

    public List<CustomItem> getIngredients(int slot) {
        return ingredients.getOrDefault(slot, new ArrayList<>());
    }

    public void setIngredient(int slot, List<CustomItem> input) {
        this.ingredients.put(slot, input);
    }

    public void setIngredient(int slot, int variant, CustomItem input) {
        if (getIngredients(slot).size() > variant) {
            getIngredients(slot).set(variant, input);
        } else {
            getIngredients(slot).add(input);
        }
    }

    public CustomAnvilRecipe.Mode getMode() {
        return mode;
    }

    public void setMode(CustomAnvilRecipe.Mode mode) {
        this.mode = mode;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
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

    public CustomAnvilRecipe.RepairCostMode getRepairCostMode() {
        return repairCostMode;
    }

    public void setRepairCostMode(CustomAnvilRecipe.RepairCostMode repairCostMode) {
        this.repairCostMode = repairCostMode;
    }
}
