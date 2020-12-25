package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

public class ExactMetaButton extends ToggleButton<CCCache> {

    public ExactMetaButton() {
        super("exact_meta", new ButtonState<>("recipe_creator", "exact_meta.enabled", Material.GREEN_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getRecipe().setExactMeta(false);
            return true;
        }), new ButtonState<>("recipe_creator", "exact_meta.disabled", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getRecipe().setExactMeta(true);
            return true;
        }));
    }
}
