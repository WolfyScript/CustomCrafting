package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;

public class AttributeModeButton extends ActionButton {

    public AttributeModeButton(AttributeModifier.Operation operation, String headURLValue) {
        super("attribute."+operation.toString().toLowerCase(Locale.ROOT), new ButtonState("attribute."+operation.toString().toLowerCase(Locale.ROOT), PlayerHeadUtils.getViaURL(headURLValue), new ButtonActionRender() {

            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                ((TestCache) guiHandler.getCustomCache()).getItems().setAttribOperation(operation);
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> replacements, GuiHandler guiHandler, Player player, ItemStack itemStack, int i, boolean b) {
                replacements.put("%C%", ((TestCache) guiHandler.getCustomCache()).getItems().getAttribOperation().equals(operation) ? "§a" : "§4");
                return itemStack;
            }
        }));
    }
}
