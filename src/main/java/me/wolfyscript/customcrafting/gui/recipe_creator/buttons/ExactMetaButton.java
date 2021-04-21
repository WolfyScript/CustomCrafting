package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

public class ExactMetaButton extends ToggleButton<CCCache> {

    public ExactMetaButton() {
        super(RecipeCreatorCluster.EXACT_META.getKey(), (cache, guiHandler, player, guiInventory, i) -> cache.getRecipe().isExactMeta(), new ButtonState<>(RecipeCreatorCluster.EXACT_META_ENABLED, Material.GREEN_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getRecipe().setExactMeta(false);
            return true;
        }), new ButtonState<>(RecipeCreatorCluster.EXACT_META_DISABLED, Material.RED_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getRecipe().setExactMeta(true);
            return true;
        }));
    }
}
