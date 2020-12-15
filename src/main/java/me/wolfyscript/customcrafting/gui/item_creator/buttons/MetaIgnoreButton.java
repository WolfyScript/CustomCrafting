package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.custom_items.Meta;
import me.wolfyscript.utilities.api.inventory.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class MetaIgnoreButton extends ActionButton {

    private final String meta;

    public MetaIgnoreButton(String meta) {
        super("meta_ignore." + meta, new ButtonState("meta_ignore", Material.CYAN_CONCRETE, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getMetaSettings().getMetaByID(meta).getOption().toString());
            return itemStack;
        }));
        this.meta = meta;
    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        Meta meta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getMetaSettings().getMetaByID(this.meta);
        List<MetaSettings.Option> options = meta.getAvailableOptions();
        int i = options.indexOf(meta.getOption()) + 1;
        if (i >= options.size()) {
            i = 0;
        }
        meta.setOption(options.get(i));
        return true;
    }
}
