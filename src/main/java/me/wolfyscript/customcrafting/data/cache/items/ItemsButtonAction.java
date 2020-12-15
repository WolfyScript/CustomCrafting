package me.wolfyscript.customcrafting.data.cache.items;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public interface ItemsButtonAction extends ButtonAction<TestCache> {

    @Override
    default boolean run(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) throws IOException {
        TestCache cache = guiHandler.getCustomCache();
        return execute(cache, cache.getItems(), guiHandler, player, inventory, i, inventoryClickEvent);
    }

    boolean execute(TestCache cache, Items items, GuiHandler<?> guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent);
}
