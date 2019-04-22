package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.recipes.FurnaceCRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;

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

    @EventHandler
    public void sddfd(FurnaceSmeltEvent event){
        /*
        System.out.print("Smelt");
        Block block = event.getBlock();
        Location loc = block.getLocation();
        RecipeHandler recipeHandler = CustomCrafting.getRecipeHandler();
        FurnaceCRecipe recipe = recipeHandler.getFurnaceRecipe(event.getSource());
        if(recipe != null){
            System.out.print("Smelted >"+recipe.getID()+"< - > "+recipe.getXp());
            event.setResult(recipe.getResult());
            CustomCrafting.getWorkbenches().addToStoredExp(loc, recipe.getXp());
        }
        */
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent event){
        /*
        Furnace furnace = (Furnace) event.getBlock().getState();
        FurnaceInventory furnaceInv = furnace.getInventory();
        RecipeHandler recipeHandler = CustomCrafting.getRecipeHandler();
        System.out.print("Test");
        if (furnaceInv.getSmelting() != null) {
            FurnaceCRecipe recipe = recipeHandler.getFurnaceRecipe(furnace.getInventory().getSmelting());
            if(recipe != null){
                System.out.print("Burning >"+recipe.getID()+"< - > "+recipe.getCookingTime());
            }
        }
        */
    }
}
