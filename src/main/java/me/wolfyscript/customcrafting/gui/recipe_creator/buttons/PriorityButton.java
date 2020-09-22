package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;

public class PriorityButton extends ActionButton {

    public PriorityButton() {
        super("priority", new ButtonState("recipe_creator", "priority", PlayerHeadUtils.getViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            RecipePriority priority = ((TestCache) guiHandler.getCustomCache()).getRecipe().getPriority();
            int order;
            order = priority.getOrder();
            if (order < 2) {
                order++;
            } else {
                order = -2;
            }
            ((TestCache) guiHandler.getCustomCache()).getRecipe().setPriority(RecipePriority.getByOrder(order));
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            RecipePriority priority = ((TestCache) guiHandler.getCustomCache()).getRecipe().getPriority();
            if (priority != null) {
                hashMap.put("%PRI%", priority.name());
            }
            return itemStack;
        }));
    }
}
