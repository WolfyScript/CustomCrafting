package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

public class AdvancedWorkbenchCondition extends Condition {

    public AdvancedWorkbenchCondition() {
        super("advanced_workbench");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
    }

    @Override
    public boolean check(ICustomRecipe<?,?> recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (recipe instanceof CraftingRecipe) {
            if (data.getBlock() != null) {
                CustomItem customItem = NamespacedKeyUtils.getCustomItem(data.getBlock());
                return customItem != null && (customItem.getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE) || customItem.getNamespacedKey().equals(CustomCrafting.ADVANCED_WORKBENCH));
            }
            return false;
        }
        return true;
    }
}
