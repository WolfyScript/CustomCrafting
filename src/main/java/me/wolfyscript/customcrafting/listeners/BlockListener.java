package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            ItemStack itemStack = event.getItemInHand();
            if (itemStack.hasItemMeta()) {
                if (itemStack.getItemMeta().hasDisplayName()) {
                    String name = itemStack.getItemMeta().getDisplayName();
                    if (name.contains(":")) {
                        name = WolfyUtilities.unhideString(name);
                        String verify = name.split(":")[1];
                        if (verify.equals("cc_workbench")) {
                            CustomCrafting.getWorkbenches().addWorkbench(event.getBlockPlaced().getLocation());
                        }
                    }
                }
                if (itemStack.getItemMeta().hasLore()) {
                    if (itemStack.getItemMeta().getLore().size() > 0) {
                        String code = WolfyUtilities.unhideString(itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1));
                        if (code.equals("cc_workbench")) {
                            CustomCrafting.getWorkbenches().addWorkbench(event.getBlockPlaced().getLocation());
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlock();
            Location location = block.getLocation();
            if (CustomCrafting.getWorkbenches().isWorkbench(location)) {
                CustomCrafting.getWorkbenches().removeWorkbench(location);
                ItemStack itemStack = CustomCrafting.getRecipeHandler().getCustomItem("customcrafting:workbench");
                block.getWorld().dropItemNaturally(block.getLocation().clone(), itemStack);
                event.setDropItems(false);
            }
        }
    }
}
