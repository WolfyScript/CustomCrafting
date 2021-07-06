package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.ItemCreatorTab;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;

public class OptionButton extends ActionButton<CCCache> {

    public OptionButton(Material material, ItemCreatorTab tab) {
        super(tab.getOptionButton(), material, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.setSubSetting("");
            cache.getItems().setCurrentTab(tab);
            return true;
        });
    }
}
