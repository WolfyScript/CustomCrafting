package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.custom_items.Meta;
import me.wolfyscript.utilities.api.inventory.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;

import java.util.List;

public class MetaIgnoreButton extends ActionButton<CCCache> {

    private final String meta;

    public MetaIgnoreButton(String meta) {
        super("meta_ignore." + meta, new ButtonState<>("meta_ignore", Material.CYAN_CONCRETE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getMetaSettings().getMetaByID(meta).getOption().toString());
            return itemStack;
        }));
        this.meta = meta;
    }

    @Override
    public boolean execute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int slot, InventoryInteractEvent event) {
        Meta meta = guiHandler.getCustomCache().getItems().getItem().getMetaSettings().getMetaByID(this.meta);
        List<MetaSettings.Option> options = meta.getAvailableOptions();
        int i = options.indexOf(meta.getOption()) + 1;
        if (i >= options.size()) {
            i = 0;
        }
        meta.setOption(options.get(i));
        return true;
    }
}
