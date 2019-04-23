package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.recipes.furnace.FurnaceCRecipe;
import me.wolfyscript.customcrafting.recipes.furnace.FurnaceFuel;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FurnaceEvents implements Listener {

    @EventHandler
    public void onDiscover(PlayerRecipeDiscoverEvent event){
        FurnaceCRecipe recipe = CustomCrafting.getRecipeHandler().getFurnaceRecipe(event.getRecipe().toString());
        if(recipe != null){
            event.setCancelled(true);
        }
    }

    private FurnaceCRecipe getAllowedFurnaceRecipe(List<FurnaceCRecipe> group, ItemStack source) {
        if (!group.isEmpty()) {
            for (FurnaceCRecipe groupRecipe : group) {
                if (groupRecipe.check(source)) {
                    return groupRecipe;
                }
            }
        }
        return null;
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent event){
        List<FurnaceFuel> fuels = CustomCrafting.getRecipeHandler().getCustomFuels();
        ItemStack input = event.getFuel();
        for (FurnaceFuel fuel : fuels) {
            if(fuel.getFuel().isSimilar(input)){
                event.setCancelled(false);
                event.setBurning(true);
                event.setBurnTime(fuel.getBurnTime());
                break;
            }
        }
    }
}
