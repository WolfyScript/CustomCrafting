package me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.conditions.WorldTimeCondition;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ConditionButton extends ActionButton<CCCache> {

    protected ConditionButton(NamespacedKey key) {
        super("conditions." + key.toString("__"), Material.REDSTONE, (cache, guiHandler, player, inventory, slot, event) -> {
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().isRightClick()) {
                var conditions = guiHandler.getCustomCache().getRecipe().getConditions();
                //Change Mode
                Condition condition = conditions.getByKey(key);
                if (condition == null) {
                    conditions.setCondition(new WorldTimeCondition());
                } else {
                    conditions.removeCondition(condition);
                }
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            return itemStack;
        });
    }
}
