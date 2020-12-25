package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItems;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EliteWorkbenchListener implements Listener {

    private final WolfyUtilities api;

    public EliteWorkbenchListener(WolfyUtilities api) {
        this.api = api;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.useInteractedBlock().equals(Event.Result.DENY) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getPlayer().isSneaking()) {
            Block block = event.getClickedBlock();
            if (CustomItems.isBlockStored(block.getLocation())) {
                CustomItem customItem = CustomItems.getStoredBlockItem(block.getLocation());
                if (customItem != null) {
                    EliteWorkbenchData eliteWorkbench = (EliteWorkbenchData) customItem.getCustomData(new NamespacedKey("customcrafting","elite_workbench"));
                    if (eliteWorkbench.isEnabled()) {
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        event.getPlayer().closeInventory();
                        ((CCCache) api.getInventoryAPI().getGuiHandler(event.getPlayer()).getCustomCache()).getEliteWorkbench().setEliteWorkbenchData(eliteWorkbench.clone());
                        api.getInventoryAPI().getGuiHandler(event.getPlayer()).openWindow(new NamespacedKey("crafting", "crafting_grid" + eliteWorkbench.getGridSize()));
                    }
                }
            }
        }
    }


}
