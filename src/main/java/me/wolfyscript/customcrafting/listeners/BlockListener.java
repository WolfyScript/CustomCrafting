package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItemBreakEvent;
import me.wolfyscript.utilities.api.custom_items.CustomItemPlaceEvent;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockListener implements Listener {

    private WolfyUtilities api;

    public BlockListener(WolfyUtilities api) {
        this.api = api;
    }

    /*
    Old Advanced Workbench registering System made compatible with new customItem placing system.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(CustomItemPlaceEvent event) {
        if (event.getCustomItem().getId().equals("customcrafting:workbench")) {
            CustomCrafting.getWorkbenches().addWorkbench(event.getBlockPlaced().getLocation());
        }
    }

    @EventHandler
    public void onBlockPlaceNonCustom(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            ItemStack itemStack = event.getItemInHand();
            if (itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasLore()) {
                    if (itemMeta.getLore().size() > 0) {
                        for (String line : itemMeta.getLore()) {
                            String code = WolfyUtilities.unhideString(line);
                            if (code.equals("cc_workbench")) {
                                CustomCrafting.getWorkbenches().addWorkbench(event.getBlockPlaced().getLocation());
                                CustomItems.setStoredBlockItem(event.getBlockPlaced().getLocation(), CustomItems.getCustomItem("customcrafting:workbench"));
                                break;
                            }
                        }
                    }
                }
            }
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
