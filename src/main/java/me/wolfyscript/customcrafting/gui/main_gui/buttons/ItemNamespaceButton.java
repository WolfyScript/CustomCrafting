package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;

public class ItemNamespaceButton extends ActionButton {

    public ItemNamespaceButton(String namespace) {
        super("namespace_" + namespace, new ButtonState("namespace", Material.CHEST, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.setListNamespace(namespace);
            return true;
        }, (values, guiHandler, player, itemStack, i, b) -> {
            values.put("%namespace%", namespace);
            return itemStack;
        }));
    }

}
