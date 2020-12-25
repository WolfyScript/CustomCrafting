package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;

public class OptionButton extends ActionButton<CCCache> {

    public OptionButton(String id, Material material, String subSetting) {
        super(id, material, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().setSubSetting(subSetting);
            return true;
        });
    }

    public OptionButton(Material material, String subSetting) {
        super(subSetting + ".option", material, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().setSubSetting(subSetting);
            return true;
        });
    }
}
