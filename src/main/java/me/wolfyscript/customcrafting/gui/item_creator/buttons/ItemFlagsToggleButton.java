package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public class ItemFlagsToggleButton extends ToggleButton<CCCache> {

    public ItemFlagsToggleButton(String flagId, ItemFlag itemFlag, Material material) {
        super("flags."+flagId, new ButtonState<>("flags."+flagId+".enabled", material, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getItems().getItem().removeItemFlags(itemFlag);
            return true;
        }), new ButtonState<>("flags."+flagId+".disabled", material, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getItems().getItem().addItemFlags(itemFlag);
            return true;
        }));
    }
}
