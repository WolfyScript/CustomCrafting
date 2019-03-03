package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
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
                    } else if (verify.equals("cc_furnace")) {
                        CustomCrafting.getWorkbenches().addFurnace(event.getBlockPlaced().getLocation());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            Location location = event.getBlock().getLocation();
            if (CustomCrafting.getWorkbenches().isFurnace(location)) {
                CustomCrafting.getWorkbenches().removeFurnace(location);
            } else if (CustomCrafting.getWorkbenches().isWorkbench(location)) {
                Collection<Entity> entities = event.getBlock().getWorld().getNearbyEntities(BoundingBox.of(event.getBlock()), entity -> (entity instanceof ArmorStand) && !entity.getCustomName().isEmpty() && entity.getCustomName().startsWith("cc:"));
                for (Entity entity : entities) {
                    entity.remove();
                }
                if(!WorkbenchContents.isOpen(location)){
                    for (ItemStack itemStack : CustomCrafting.getWorkbenches().getContents(location)) {
                        if(!itemStack.getType().equals(Material.AIR))
                            location.getWorld().dropItemNaturally(location.clone().add(0.5,0, 0.5), itemStack);
                    }
                }
                CustomCrafting.getWorkbenches().removeWorkbench(location);
                WorkbenchContents.close(location);
            }
        }
    }
}
