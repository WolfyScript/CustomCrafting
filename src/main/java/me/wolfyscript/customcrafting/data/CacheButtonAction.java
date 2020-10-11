package me.wolfyscript.customcrafting.data;

import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public interface CacheButtonAction extends ButtonAction {

    @Override
    default boolean run(GuiHandler<?> guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) throws IOException {
        return execute(((TestCache) guiHandler.getCustomCache()), guiHandler, player, inventory, i, inventoryClickEvent);
    }

    boolean execute(TestCache cache, GuiHandler<?> guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent);
}
