package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingRecipe;
import org.bukkit.Location;

public class AdvancedWorkbenchCondition extends Condition {

    public AdvancedWorkbenchCondition() {
        super("advanced_workbench");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
    }

    @Override
    public boolean check(ICustomRecipe recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (recipe instanceof CraftingRecipe) {
            if (data.getBlock() != null) {
                Location location = data.getBlock().getLocation();
                return CustomCrafting.getWorkbenches().isWorkbench(location);
            }
            return false;
        }
        return true;
    }
}
