package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;

public class AdvancedWorkbenchCondition extends Condition {

    public AdvancedWorkbenchCondition() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "advanced_workbench"));
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (recipe instanceof CraftingRecipe) {
            if (data.getBlock() != null) {
                var customItem = NamespacedKeyUtils.getCustomItem(data.getBlock());
                return customItem != null && (customItem.getNamespacedKey().equals(CustomCrafting.INTERNAL_ADVANCED_CRAFTING_TABLE) || customItem.getNamespacedKey().equals(CustomCrafting.ADVANCED_WORKBENCH));
            }
            return false;
        }
        return true;
    }
}
