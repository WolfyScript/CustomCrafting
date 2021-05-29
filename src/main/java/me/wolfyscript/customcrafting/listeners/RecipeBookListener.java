package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.gui.RecipeBookCluster;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class RecipeBookListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onClickBottomInv(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory instanceof GUIInventory) {
            GUIInventory<?> inventory1 = (GUIInventory<?>) inventory;
            if (inventory1.getWindow().getNamespacedKey().equals(RecipeBookCluster.RECIPE_BOOK)) {
                switch (event.getAction()) {
                    case DROP_ALL_CURSOR:
                    case DROP_ALL_SLOT:
                    case DROP_ONE_CURSOR:
                    case DROP_ONE_SLOT:
                        event.setCancelled(true);
                        break;
                    default:
                        //Nothing
                }
            }
        }
    }
}
