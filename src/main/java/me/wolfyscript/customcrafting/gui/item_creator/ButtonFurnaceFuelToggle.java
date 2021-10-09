package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

public class ButtonFurnaceFuelToggle extends ToggleButton<CCCache> {

    public ButtonFurnaceFuelToggle(String id, Material material) {
        super("fuel." + id, (cache, guiHandler, player, guiInventory, i) -> cache.getItems().getItem().getFuelSettings().getAllowedBlocks().contains(material), new ButtonState<>("fuel." + id + ".enabled", material, (ItemsButtonAction) (testCache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().getFuelSettings().getAllowedBlocks().remove(material);
            return true;
        }), new ButtonState<>("fuel." + id + ".disabled", material, (ItemsButtonAction) (testCache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().getFuelSettings().getAllowedBlocks().add(material);
            return true;
        }));
    }
}
