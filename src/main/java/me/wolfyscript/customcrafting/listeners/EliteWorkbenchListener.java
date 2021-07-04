package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.world.WorldUtils;
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
            var block = event.getClickedBlock();
            if (block != null && WorldUtils.getWorldCustomItemStore().isStored(block.getLocation())) {
                var customItem = NamespacedKeyUtils.getCustomItem(block);
                if (customItem != null) {
                    EliteWorkbenchData eliteWorkbench = (EliteWorkbenchData) customItem.getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE);
                    if (eliteWorkbench != null && eliteWorkbench.isEnabled()) {
                        event.setCancelled(true);
                        ((CCCache) api.getInventoryAPI().getGuiHandler(event.getPlayer()).getCustomCache()).getEliteWorkbench().setEliteWorkbenchData(eliteWorkbench.clone());
                        api.getInventoryAPI().getGuiHandler(event.getPlayer()).openWindow(new NamespacedKey("crafting", "crafting_grid" + eliteWorkbench.getGridSize()));
                    }
                }
            }
        }
    }

}
