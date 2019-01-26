package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.ShapedCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ShapedRecipe;

public class Events implements Listener {

    private WolfyUtilities api;

    public Events(WolfyUtilities api) {
        this.api = api;
    }

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {


        }

    }

    @EventHandler
    public void onPreCraftNew(PrepareItemCraftEvent e) {
        if (e.getRecipe() != null) {
            if (e.getRecipe() instanceof ShapedRecipe) {
                ShapedCraftRecipe recipe = CustomCrafting.getRecipeHandler().getShapedRecipe(((ShapedRecipe) e.getRecipe()).getKey().getKey());



            }

        }
    }


}
