package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

class ButtonExactMeta extends ToggleButton<CCCache> {

    ButtonExactMeta() {
        super(ClusterRecipeCreator.EXACT_META.getKey(), (cache, guiHandler, player, guiInventory, i) -> cache.getRecipeCreatorCache().getRecipeCache().isExactMeta(), new ButtonState<>(ClusterRecipeCreator.EXACT_META_ENABLED, Material.GREEN_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getRecipeCache().setExactMeta(false);
            return true;
        }), new ButtonState<>(ClusterRecipeCreator.EXACT_META_DISABLED, Material.RED_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getRecipeCache().setExactMeta(true);
            return true;
        }));
    }
}
