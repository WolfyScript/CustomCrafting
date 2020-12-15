package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

public class ExactMetaButton extends ToggleButton<TestCache> {

    public ExactMetaButton() {
        super("exact_meta", new ButtonState<>("recipe_creator", "exact_meta.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.getCustomCache().getRecipe().setExactMeta(false);
            return true;
        }), new ButtonState<>("recipe_creator", "exact_meta.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.getCustomCache().getRecipe().setExactMeta(true);
            return true;
        }));
    }
}
