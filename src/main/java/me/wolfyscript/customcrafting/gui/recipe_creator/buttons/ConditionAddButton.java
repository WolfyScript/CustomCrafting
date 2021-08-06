package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;

public class ConditionAddButton extends ActionButton<CCCache> {

    public ConditionAddButton(Condition<?> condition) {
        super("icon_" + condition.getNamespacedKey().toString("_"), new ButtonState<>("icon", condition.getGuiComponent().getIcon(), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipe().getConditions().setCondition(condition);
            return true;
        }, (values, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            var langAPI = guiHandler.getApi().getLanguageAPI();
            values.put("%name%", langAPI.replaceColoredKeys(condition.getGuiComponent().getName()));
            values.put("%description%", langAPI.replaceColoredKeys(condition.getGuiComponent().getDescription()));
            return itemStack;
        }));
    }
}