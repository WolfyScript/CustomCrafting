package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.gui.RecipeBookCluster;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class RecipeBookListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onClickBottomInv(InventoryClickEvent event) {
        if (event.getInventory() instanceof GUIInventory<?> inventory && inventory.getWindow().getNamespacedKey().equals(RecipeBookCluster.RECIPE_BOOK) && Objects.equals(event.getClickedInventory(), event.getView().getBottomInventory())) {
            event.setCancelled(true);
        }
    }
}
