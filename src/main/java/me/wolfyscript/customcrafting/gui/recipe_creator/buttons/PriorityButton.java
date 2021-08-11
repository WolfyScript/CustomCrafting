package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

public class PriorityButton extends ActionButton<CCCache> {

    public PriorityButton() {
        super(RecipeCreatorCluster.PRIORITY.getKey(), PlayerHeadUtils.getViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"), (cache, guiHandler, player, inventory, slot, event) -> {
            RecipePriority priority = cache.getRecipeCreatorCache().getRecipeCache().getPriority();
            int order;
            order = priority.getOrder();
            if (order < 2) {
                order++;
            } else {
                order = -2;
            }
            cache.getRecipeCreatorCache().getRecipeCache().setPriority(RecipePriority.getByOrder(order));
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            RecipePriority priority = cache.getRecipeCreatorCache().getRecipeCache().getPriority();
            if (priority != null) {
                hashMap.put("%PRI%", priority.name());
            }
            return itemStack;
        });
    }
}
