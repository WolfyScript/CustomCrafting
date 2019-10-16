package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CauldronListener implements Listener {

    private WolfyUtilities api;
    private List<Material> materials = new ArrayList<>();

    public CauldronListener(WolfyUtilities api) {
        this.api = api;
        materials.add(Material.NETHERRACK);
        materials.add(Material.FIRE);
        materials.add(Material.CAULDRON);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

    }

    @EventHandler
    public void onTest(ItemSpawnEvent event) {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (materials.contains(block.getType())) {
            int y = block.getType().equals(Material.CAULDRON) ? 2 : 0;
            Location location = block.getLocation().subtract(0,y,0).add(0, 2, 0);
            if (CustomCrafting.getCauldrons().isCauldron(location)) {
                CustomCrafting.getCauldrons().removeCauldron(location);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (materials.contains(block.getType())) {
            int y = 0;
            switch (block.getType()) {
                case CAULDRON:
                    y = 2;
                    break;
                case FIRE:
                    y = 1;
            }
            Location location = block.getLocation().subtract(0, y, 0);
            for (int i = 0; i < 3; i++) {
                location.add(0, i > 0 ? 1 : 0, 0);
                if (!location.getBlock().getType().equals(materials.get(i))) {
                    if (CustomCrafting.getCauldrons().isCauldron(location)) {
                        CustomCrafting.getCauldrons().removeCauldron(location);
                    }
                    return;
                }
            }
            CustomCrafting.getCauldrons().addCauldron(location);
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            Block block = p.getTargetBlock(null, 5);
            if(block.getType().equals(Material.FIRE)){
                Location location = block.getLocation().subtract(0,1,0).add(0, 2, 0);
                if (CustomCrafting.getCauldrons().isCauldron(location)) {
                    CustomCrafting.getCauldrons().removeCauldron(location);
                }
            }
        }
    }
}
