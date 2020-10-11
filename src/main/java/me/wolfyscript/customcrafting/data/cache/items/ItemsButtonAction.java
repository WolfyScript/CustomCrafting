package me.wolfyscript.customcrafting.data.cache.items;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public interface ItemsButtonAction extends ButtonAction {

    @Override
    default boolean run(GuiHandler<?> guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) throws IOException {
        TestCache cache = ((TestCache) guiHandler.getCustomCache());
        return execute(cache, cache.getItems(), guiHandler, player, inventory, i, inventoryClickEvent);
    }

    boolean execute(TestCache cache, Items items, GuiHandler<?> guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent);
}
