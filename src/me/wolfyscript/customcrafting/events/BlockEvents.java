package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.util.Collection;

public class BlockEvents implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            ItemStack itemStack = event.getItemInHand();
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                String name = itemStack.getItemMeta().getDisplayName();
                if (name.contains(":")) {
                    name = WolfyUtilities.unhideString(name);
                    String verify = name.split(":")[1];
                    if (verify.equals("cc_workbench")) {
                        CustomCrafting.getWorkbenches().addWorkbench(event.getBlockPlaced().getLocation());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlock();
            Location location = block.getLocation();
            if (CustomCrafting.getWorkbenches().isWorkbench(location)) {
                CustomCrafting.getWorkbenches().removeWorkbench(location);
                block.getWorld().dropItemNaturally(block.getLocation().clone(), CustomCrafting.getRecipeHandler().getCustomItem("customcrafting","workbench"));
                event.setDropItems(false);
            }
        }
    }
}
