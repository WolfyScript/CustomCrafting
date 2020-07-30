package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public class ItemFlagsToggleButton extends ToggleButton {

    public ItemFlagsToggleButton(String flagId, ItemFlag itemFlag, Material material) {
        super("flags."+flagId, new ButtonState("flags."+flagId+".enabled", material, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getItems().getItem().removeItemFlags(itemFlag);
            return true;
        }), new ButtonState("flags."+flagId+".disabled", material, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getItems().getItem().addItemFlags(itemFlag);
            return true;
        }));
    }
}
