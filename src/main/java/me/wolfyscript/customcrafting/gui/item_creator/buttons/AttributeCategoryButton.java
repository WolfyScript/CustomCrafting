package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;

public class AttributeCategoryButton extends ActionButton<CCCache> {

    public AttributeCategoryButton(String attribute, Material material) {
        super("attribute."+attribute, material, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().setSubSetting("attribute."+attribute);
            return true;
        });
    }
}
