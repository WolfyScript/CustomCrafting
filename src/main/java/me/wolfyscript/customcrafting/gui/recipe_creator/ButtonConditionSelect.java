package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;

class ButtonConditionSelect extends ActionButton<CCCache> {

    ButtonConditionSelect(NamespacedKey key) {
        super("icon_" + key.toString("_"), new ButtonState<>("select", Condition.getGuiComponent(key).getIcon(), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getConditionsCache().setSelectedCondition(key);
            return true;
        }, (values, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            var langAPI = guiHandler.getApi().getLanguageAPI();
            Condition.AbstractGUIComponent<?> guiComponent = Condition.getGuiComponent(key);
            if (guiComponent != null) {
                values.put("%name%", langAPI.replaceColoredKeys(guiComponent.getName()));
                values.put("%description%", langAPI.replaceColoredKeys(guiComponent.getDescription()));
            }
            return itemStack;
        }));
    }
}
