package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.attribute.AttributeModifier;

import java.util.Locale;

public class ButtonAttributeMode extends ActionButton<CCCache> {

    public ButtonAttributeMode(AttributeModifier.Operation operation, String headURLValue) {
        super("attribute."+operation.toString().toLowerCase(Locale.ROOT), PlayerHeadUtils.getViaURL(headURLValue), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getItems().setAttribOperation(operation);
            return true;
        }, (replacements, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            replacements.put("%C%", guiHandler.getCustomCache().getItems().getAttribOperation().equals(operation) ? "§a" : "§4");
            return itemStack;
        });
    }
}
