package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbench;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EliteWorkbenchListener implements Listener {

    private WolfyUtilities api;

    public EliteWorkbenchListener(WolfyUtilities api){
        this.api = api;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            Block block = event.getClickedBlock();
            if(CustomItems.isBlockStored(block.getLocation())){
                CustomItem customItem = CustomItems.getStoredBlockItem(block.getLocation());
                if(customItem != null){
                    EliteWorkbench eliteWorkbench = (EliteWorkbench) customItem.getCustomData("elite_workbench");
                    if(eliteWorkbench.isEnabled()){
                        event.setCancelled(true);
                        api.getInventoryAPI().getGuiHandler(event.getPlayer()).changeToInv("crafting", "crafting_grid"+eliteWorkbench.getGridSize());
                    }
                }
            }
        }
    }


}
