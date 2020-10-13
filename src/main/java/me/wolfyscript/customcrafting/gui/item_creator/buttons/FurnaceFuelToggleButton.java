package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import org.bukkit.Material;

public class FurnaceFuelToggleButton extends ToggleButton {

    public FurnaceFuelToggleButton(String id, Material material) {
        super("fuel." + id, new ButtonState("fuel." + id + ".enabled", material, (ItemsButtonAction) (testCache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().getAllowedBlocks().remove(material);
            return true;
        }), new ButtonState("fuel." + id + ".disabled", material, (ItemsButtonAction) (testCache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().getAllowedBlocks().add(material);
            return true;
        }));
    }
}
