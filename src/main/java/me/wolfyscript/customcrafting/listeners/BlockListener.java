package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.events.CustomItemBreakEvent;
import me.wolfyscript.utilities.util.events.CustomItemPlaceEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BlockListener implements Listener {

    private final WolfyUtilities api;

    public BlockListener(WolfyUtilities api) {
        this.api = api;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(CustomItemPlaceEvent event) {
        if (event.getCustomItem().hasNamespacedKey() && event.getCustomItem().getNamespacedKey().equals(NamespacedKey.getByString("customcrafting:workbench"))) {
            CustomCrafting.getWorkbenches().addWorkbench(event.getBlockPlaced().getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(CustomItemBreakEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlock();
            Location location = block.getLocation();
            if (CustomCrafting.getWorkbenches().isWorkbench(location)) {
                CustomCrafting.getWorkbenches().removeWorkbench(location);
            }
        }
    }
}
