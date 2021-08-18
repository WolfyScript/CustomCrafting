package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

import java.util.List;

public class AdvancedWorkbenchCondition extends Condition<AdvancedWorkbenchCondition> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "advanced_workbench");

    public AdvancedWorkbenchCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT);
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        if (recipe instanceof CraftingRecipe) {
            if (data.getBlock() != null) {
                var customItem = NamespacedKeyUtils.getCustomItem(data.getBlock());
                return customItem != null && (customItem.getNamespacedKey().equals(CustomCrafting.INTERNAL_ADVANCED_CRAFTING_TABLE) || customItem.getNamespacedKey().equals(CustomCrafting.ADVANCED_WORKBENCH));
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isApplicable(ICustomRecipe<?> recipe) {
        return RecipeType.Container.CRAFTING.isInstance(recipe);
    }

    public static class GUIComponent extends IconGUIComponent<AdvancedWorkbenchCondition> {

        public GUIComponent() {
            super(Material.CRAFTING_TABLE, getLangKey("advanced_crafting_table", "name"), List.of(getLangKey("advanced_crafting_table", "description")));
        }

        @Override
        public boolean shouldRender(RecipeType<?> type) {
            return RecipeType.Container.CRAFTING.has(type);
        }
    }
}
