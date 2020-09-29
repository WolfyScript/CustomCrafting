package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Material;

public class OptionButton extends ActionButton {

    public OptionButton(String id, Material material, String subSetting) {
        super(id, material, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting(subSetting);
            return true;
        });
    }

    public OptionButton(Material material, String subSetting) {
        super(subSetting + ".option", material, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting(subSetting);
            return true;
        });
    }
}
