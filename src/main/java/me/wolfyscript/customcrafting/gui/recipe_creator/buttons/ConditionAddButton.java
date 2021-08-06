package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;

public class ConditionAddButton extends ActionButton<CCCache> {

    public ConditionAddButton(Condition<?> condition, String id, Material material, String option) {
        super(id, material, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipe().getConditions().setCondition(condition);
            return true;
        });
    }
}
