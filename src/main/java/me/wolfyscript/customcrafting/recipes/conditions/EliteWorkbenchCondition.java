package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbench;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EliteWorkbenchCondition extends Condition {

    private List<String> eliteWorkbenches;

    public EliteWorkbenchCondition() {
        super("elite_workbench");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
        this.eliteWorkbenches = new ArrayList<>();
    }

    @Override
    public boolean check(CustomRecipe recipe, Conditions.Data data) {
        if(option.equals(Conditions.Option.IGNORE)){
            return true;
        }
        if (recipe instanceof EliteCraftingRecipe) {
            if(data.getBlock() != null){
                Location location = data.getBlock().getLocation();
                CustomItem customItem = CustomItems.getStoredBlockItem(location);
                if(customItem != null){
                    if (eliteWorkbenches.contains(customItem.getId())){
                        EliteWorkbench eliteWorkbenchData = (EliteWorkbench) customItem.getCustomData("elite_workbench");
                        return eliteWorkbenchData.isEnabled();
                    }
                }
            }
            return false;
        }
        return true;
    }

    public void addEliteWorkbenches(String eliteWorkbenches) {
        if(!this.eliteWorkbenches.contains(eliteWorkbenches)){
            this.eliteWorkbenches.add(eliteWorkbenches);
        }
    }

    public List<String> getEliteWorkbenches() {
        return eliteWorkbenches;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(option.toString());
        stringBuilder.append(";");
        for(String eliteWorkbench : eliteWorkbenches){
            stringBuilder.append(eliteWorkbench).append(",");
        }
        return stringBuilder.toString();
    }

    @Override
    public void fromString(String value) {
        String[] args = value.split(";");
        this.option = Conditions.Option.valueOf(args[0]);
        if(args.length > 1 && args[1].contains(",")){
            this.eliteWorkbenches = Arrays.asList(args[1].split(","));
        }
    }
}
