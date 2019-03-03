package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.FurnaceCRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FurnaceEvents implements Listener {

    @EventHandler
    public void onSmelting(FurnaceSmeltEvent event) {
        FurnaceCRecipe recipe = CustomCrafting.getRecipeHandler().getFurnaceRecipe(event.getSource());
        if (recipe != null) {
            if (!recipe.check(event.getSource())) {
                FurnaceCRecipe furnaceRecipe = getAllowedFurnaceRecipe(CustomCrafting.getRecipeHandler().getFurnaceRecipes(event.getSource()), event.getSource());
                if (furnaceRecipe != null && checkFurnace(event.getBlock().getLocation(), recipe)) {
                    //Recipe Allowed
                    event.setResult(furnaceRecipe.getResult());
                } else {
                    event.setCancelled(true);
                }
            } else if (checkFurnace(event.getBlock().getLocation(), recipe)) {
                //Recipe Allowed
                event.setResult(recipe.getResult());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBurning(FurnaceBurnEvent event) {

    }

    private boolean checkFurnace(Location location, FurnaceCRecipe recipe) {
        return !recipe.needsAdvancedFurnace() || (location != null && CustomCrafting.getWorkbenches().isFurnace(location));
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
}
