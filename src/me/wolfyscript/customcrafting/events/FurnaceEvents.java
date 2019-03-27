package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.FurnaceCRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;

import java.util.List;

public class FurnaceEvents implements Listener {

    @EventHandler
    public void onDiscover(PlayerRecipeDiscoverEvent event){
        FurnaceCRecipe recipe = CustomCrafting.getRecipeHandler().getFurnaceRecipe(event.getRecipe().toString());
        if(recipe != null){
            event.setCancelled(true);
        }
    }

    /*
    private boolean checkFurnace(Location location, FurnaceCRecipe recipe) {
        return !recipe.needsAdvancedFurnace() || (location != null && CustomCrafting.getWorkbenches().isFurnace(location));
    }
    */

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
}
