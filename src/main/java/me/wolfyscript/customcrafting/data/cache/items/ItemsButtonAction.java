package me.wolfyscript.customcrafting.data.cache.items;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonAction;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;

import java.io.IOException;

public interface ItemsButtonAction extends ButtonAction<CCCache> {

    @Override
    default boolean run(CCCache cache, GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int i, InventoryInteractEvent inventoryClickEvent) throws IOException {
        return execute(cache, cache.getItems(), guiHandler, player, inventory, i, inventoryClickEvent);
    }

    boolean execute(CCCache cache, Items items, GuiHandler<?> guiHandler, Player player, GUIInventory<CCCache> inventory, int i, InventoryInteractEvent inventoryClickEvent);
}
