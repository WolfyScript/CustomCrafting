package me.wolfyscript.customcrafting.data;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public interface CacheButtonAction extends ButtonAction<TestCache> {

    @Override
    default boolean run(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) throws IOException {
        return execute(guiHandler.getCustomCache(), guiHandler, player, inventory, i, inventoryClickEvent);
    }

    boolean execute(TestCache cache, GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent);
}
