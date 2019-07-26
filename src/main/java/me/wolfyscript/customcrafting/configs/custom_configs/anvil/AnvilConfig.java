package me.wolfyscript.customcrafting.configs.custom_configs.anvil;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.config.ConfigAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AnvilConfig extends CustomConfig {

    public AnvilConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, folder, name, CustomCrafting.getConfigHandler().getConfig().getPreferredFileType());
    }

    public AnvilConfig(ConfigAPI configAPI, String folder, String name, String fileType) {
        super(configAPI, folder, "anvil", name, "anvil", fileType);
    }

    public void setPermission(boolean perm) {
        set("permissions", perm);
    }

    public boolean needPerm() {
        return getBoolean("permissions");
    }

    public int getRepairCost(){
        return getInt("repair_cost.amount");
    }

    public void setRepairCost(int repairCost){
        set("repair_cost.amount", repairCost);
    }

    public boolean isApplyRepairCost(){
        return getBoolean("repair_cost.apply_to_result");
    }

    public void setApplyRepairCost(boolean apply){
        set("repair_cost.apply_to_result", apply);
    }

    public CustomAnvilRecipe.RepairCostMode getRepairCostMode(){
        if(getString("repair_cost_mode") != null && !getString("repair_cost_mode").isEmpty()){
            return CustomAnvilRecipe.RepairCostMode.valueOf(getString("repair_cost_mode"));
        }
        return CustomAnvilRecipe.RepairCostMode.NONE;
    }

    public void setRepairCostMode(CustomAnvilRecipe.RepairCostMode mode){
        set("repair_cost_mode", mode.toString());
    }

    public boolean isBlockRepairing(){
        return getBoolean("block_repairing");
    }

    public void setBlockRepairing(boolean blockRepairing){
        set("block_repairing", blockRepairing);
    }

    public boolean isBlockRename(){
        return getBoolean("block_rename");
    }

    public void setBlockRename(boolean blockRename){
        set("block_rename", blockRename);
    }

    public boolean isBlockEnchant(){
        return getBoolean("block_enchant");
    }

    public void setBlockEnchant(boolean blockEnchant){
        set("block_enchant", blockEnchant);
    }

    public CustomAnvilRecipe.Mode getMode(){
        return CustomAnvilRecipe.Mode.valueOf(getString("mode.usedMode"));
    }

    public void setMode(CustomAnvilRecipe.Mode mode){
        set("mode.usedMode", mode.toString());
    }

    public CustomItem getResult(){
        return getCustomItem("mode.result");
    }

    public void setResult(CustomItem customItem){
        saveCustomItem("mode.result", customItem);
    }

    public int getDurability(){
        return getInt("mode.durability");
    }

    public void setDurability(int dur){
        set("mode.durability", dur);
    }

    public List<CustomItem> getInputLeft(){
        return getInput("left");
    }

    public List<CustomItem> getInputRight(){
        return getInput("right");
    }

    private List<CustomItem> getInput(String leftRight){
        List<CustomItem> result = new ArrayList<>();
        if(get("input_"+leftRight) != null){
            Set<String> variants = getValues("input_"+leftRight).keySet();
            for(String variant : variants){
                result.add(getCustomItem("input_"+leftRight+"."+variant));
            }
        }
        return result;
    }

    public void setInputLeft(List<CustomItem> inputs){
        setInput("left", inputs);
    }

    public void setInputRight(List<CustomItem> inputs){
        setInput("right", inputs);
    }

    private void setInput(String slot, List<CustomItem> inputs){
        int variant = 0;
        for(CustomItem customItem : inputs){
            saveCustomItem("input_"+slot+".var"+(variant++), customItem);
        }
    }

    /*
    TODO:
     MAKE REPAIR_COST applying toggleable!
     Maybe add a option to ignore it from the input or add the repair cost from the input to the result!
     */
}

