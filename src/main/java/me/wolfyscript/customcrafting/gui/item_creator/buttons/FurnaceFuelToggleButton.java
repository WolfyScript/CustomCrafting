package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

public class FurnaceFuelToggleButton extends ToggleButton<CCCache> {

    public FurnaceFuelToggleButton(String id, Material material) {
        super("fuel." + id, update -> update.getGuiHandler().getCustomCache().getItems().getItem().getAllowedBlocks().contains(Material.FURNACE), new ButtonState<>("fuel." + id + ".enabled", material, (ItemsButtonAction) (testCache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().getAllowedBlocks().remove(material);
            return true;
        }), new ButtonState<>("fuel." + id + ".disabled", material, (ItemsButtonAction) (testCache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().getAllowedBlocks().add(material);
            return true;
        }));
    }
}
