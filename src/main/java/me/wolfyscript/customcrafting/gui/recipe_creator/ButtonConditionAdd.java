package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CCClassRegistry;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;

class ButtonConditionAdd extends ActionButton<CCCache> {

    ButtonConditionAdd(NamespacedKey key, Condition.AbstractGUIComponent<?> condition) {
        super("icon_" + key.toString("_"), new ButtonState<>("icon", condition.getIcon(), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getRecipeCache().getConditions().setCondition(CCClassRegistry.RECIPE_CONDITIONS.create(key));
            return true;
        }, (values, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            var langAPI = guiHandler.getApi().getLanguageAPI();
            values.put("%name%", langAPI.replaceColoredKeys(condition.getName()));
            values.put("%description%", langAPI.replaceColoredKeys(condition.getDescription()));
            return itemStack;
        }));
    }
}
