package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;

public class HiddenButton extends ToggleButton {

    public HiddenButton() {
        super("hidden", new ButtonState("recipe_creator", "hidden.enabled", PlayerHeadUtils.getViaURL("ce9d49dd09ecee2a4996965514d6d301bf12870c688acb5999b6658e1dfdff85"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getRecipe().setHidden(false);
            return true;
        }), new ButtonState("recipe_creator", "hidden.disabled", PlayerHeadUtils.getViaURL("85e5bf255d5d7e521474318050ad304ab95b01a4af0bae15e5cd9c1993abcc98"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getRecipe().setHidden(true);
            return true;
        }));
    }
}
