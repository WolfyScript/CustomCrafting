package me.wolfyscript.customcrafting.configs.custom_configs.anvil;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.config.ConfigAPI;

import java.util.HashMap;
import java.util.Set;

public class AnvilConfig extends CustomConfig {

    public AnvilConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, "anvil", folder, "anvil", name);
    }

    public void setPermission(boolean perm) {
        set("permissions", perm);
    }

    public boolean needPerm() {
        return getBoolean("permissions");
    }

    public int getRepairCost(){
        return getInt("repair_cost");
    }

    public void setRepairCost(int repairCost){
        set("repair_cost", repairCost);
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
        if(getConfig().get("mode.durability") != null){
            return CustomAnvilRecipe.Mode.DURABILITY;
        }else if(getConfig().getConfigurationSection("mode.result") != null){
            return CustomAnvilRecipe.Mode.RESULT;
        }
        return CustomAnvilRecipe.Mode.NONE;
    }

    public CustomItem getResult(){
        return getCustomItem("mode.result");
    }

    public int getDurability(){
        return getInt("mode.durability");
    }

    public HashMap<CustomItem, Boolean> getInputLeft(){
        return getInput("left");
    }

    public HashMap<CustomItem, Boolean> getInputRight(){
        return getInput("right");
    }

    private HashMap<CustomItem, Boolean> getInput(String leftRight){
        HashMap<CustomItem, Boolean> result = new HashMap<>();
        if(getConfig().getConfigurationSection("input_"+leftRight) != null){
            Set<String> variants = getConfig().getConfigurationSection("input_"+leftRight).getKeys(false);
            for(String variant : variants){
                result.put(getCustomItem("input_"+leftRight+"."+variant+".item"), getBoolean("input_"+leftRight+"."+variant+".ignore_consume"));
            }
        }
        return result;
    }



}

