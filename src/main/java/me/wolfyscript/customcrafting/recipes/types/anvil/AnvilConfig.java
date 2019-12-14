package me.wolfyscript.customcrafting.recipes.types.anvil;

import com.google.gson.JsonObject;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AnvilConfig extends RecipeConfig {

    public AnvilConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, folder, name, "json");
    }

    public AnvilConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        super(configAPI, folder, "anvil", name, "anvil", fileType);
    }

    public AnvilConfig(String jsonData, ConfigAPI configAPI, String namespace, String key) {
        super(jsonData, configAPI, namespace, "anvil", key, "anvil");
    }

    public AnvilConfig() {
        super("anvil");
    }

    public void setPermission(boolean perm) {
        set("permissions", perm);
    }

    public boolean needPerm() {
        return getBoolean("permissions");
    }

    public int getRepairCost() {
        return getInt("repair_cost.amount");
    }

    public void setRepairCost(int repairCost) {
        set("repair_cost.amount", repairCost);
    }

    public boolean isApplyRepairCost() {
        return getBoolean("repair_cost.apply_to_result");
    }

    public void setApplyRepairCost(boolean apply) {
        set("repair_cost.apply_to_result", apply);
    }

    public CustomAnvilRecipe.RepairCostMode getRepairCostMode() {
        if (getString("repair_cost.mode") != null && !getString("repair_cost.mode").isEmpty()) {
            return CustomAnvilRecipe.RepairCostMode.valueOf(getString("repair_cost.mode"));
        }
        return CustomAnvilRecipe.RepairCostMode.NONE;
    }

    public void setRepairCostMode(CustomAnvilRecipe.RepairCostMode mode) {
        set("repair_cost.mode", mode.toString());
    }

    public boolean isBlockRepairing() {
        return getBoolean("block_repairing");
    }

    public void setBlockRepairing(boolean blockRepairing) {
        set("block_repairing", blockRepairing);
    }

    public boolean isBlockRename() {
        return getBoolean("block_rename");
    }

    public void setBlockRename(boolean blockRename) {
        set("block_rename", blockRename);
    }

    public boolean isBlockEnchant() {
        return getBoolean("block_enchant");
    }

    public void setBlockEnchant(boolean blockEnchant) {
        set("block_enchant", blockEnchant);
    }

    public CustomAnvilRecipe.Mode getMode() {
        return CustomAnvilRecipe.Mode.valueOf(getString("mode.usedMode"));
    }

    public void setMode(CustomAnvilRecipe.Mode mode) {
        set("mode.usedMode", mode.toString());
    }

    @Override
    public List<CustomItem> getResult() {
        return getResult("mode");
    }

    @Override
    public void setResult(List<CustomItem> results) {
        setResult("mode", results);
    }

    public int getDurability() {
        return getInt("mode.durability");
    }

    public void setDurability(int dur) {
        set("mode.durability", dur);
    }

    public List<CustomItem> getInputLeft() {
        return getInput("left");
    }

    public List<CustomItem> getInputRight() {
        return getInput("right");
    }

    public List<CustomItem> getInput(int slot) {
        if (slot == 0) {
            return getInputLeft();
        } else if (slot == 1) {
            return getInputRight();
        } else {
            return getResult();
        }
    }

    public void setInput(int slot, List<CustomItem> inputs) {
        if (slot == 0) {
            setInputLeft(inputs);
        } else if (slot == 1) {
            setInputRight(inputs);
        } else {
            setResult(inputs);
        }
    }

    public List<CustomItem> getInput(String leftRight) {
        List<CustomItem> result = new ArrayList<>();
        if (get("input_" + leftRight) != null) {
            Set<String> variants = getValues("input_" + leftRight).keySet();
            for (String variant : variants) {
                result.add(getCustomItem("input_" + leftRight + "." + variant));
            }
        }
        return result;
    }

    public void setInputLeft(List<CustomItem> inputs) {
        setInput("left", inputs);
    }

    public void setInputRight(List<CustomItem> inputs) {
        setInput("right", inputs);
    }

    private void setInput(String slot, List<CustomItem> inputs) {
        set("input_" + slot, new JsonObject());
        int variant = 0;
        for (CustomItem customItem : inputs) {
            saveCustomItem("input_" + slot + ".var" + (variant++), customItem);
        }
    }

    /*
    TODO:
     MAKE REPAIR_COST applying toggleable!
     Maybe add a option to ignore it from the input or add the repair cost from the input to the result!
     */
}

