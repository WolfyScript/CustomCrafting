package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;

public class AttributeCategoryButton extends ActionButton {

    public AttributeCategoryButton(String attribute, Material material) {
        super("attribute."+attribute, material, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("attribute."+attribute);
            return true;
        });
    }
}
