package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.Meta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.MetaSettings;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

import java.util.List;

public class MetaIgnoreButton extends ActionButton<CCCache> {

    public MetaIgnoreButton(NamespacedKey metaKey) {
        super("meta_ignore." + metaKey.toString("."), new ButtonState<>("meta_ignore", Material.CYAN_CONCRETE, (cache, guiHandler, player, guiInventory, slot, inventoryInteractEvent) -> {
            Meta meta = guiHandler.getCustomCache().getItems().getItem().getMetaSettings().get(metaKey);
            List<MetaSettings.Option> options = meta.getAvailableOptions();
            int i = options.indexOf(meta.getOption()) + 1;
            if (i >= options.size()) {
                i = 0;
            }
            meta.setOption(options.get(i));
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getMetaSettings().get(metaKey).getOption().toString());
            return itemStack;
        }));
    }
}
