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
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FurnaceEvents implements Listener {

    @EventHandler
    public void onSmelting(FurnaceSmeltEvent event) {
        if(event.getResult().hasItemMeta() && event.getResult().getItemMeta().hasDisplayName()){
            String name = WolfyUtilities.unhideString(event.getResult().getItemMeta().getDisplayName());
            if(name.contains(";/id:")){
                System.out.println(name);
                String key = name.split(";/id:")[1];
                FurnaceCRecipe recipe = CustomCrafting.getRecipeHandler().getFurnaceRecipe(key);
                if (recipe != null) {
                    if (recipe.check(event.getSource())) {
                        System.out.println("SMELTED: "+key);
                        Location location = event.getBlock().getLocation();
                        System.out.println(location.getBlock().getClass().getName() + " "+location.getBlock().getClass().getSuperclass().getName());
                        System.out.println(event.getBlock().getClass().getName() + " "+event.getBlock().getClass().getSuperclass().getName());
                        //furnace.setCustomName("§4TEST");
                    } else{
                        //Recipe DENIED
                        event.setCancelled(true);
                        event.setResult(new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBurning(FurnaceBurnEvent event) {

    }

    @EventHandler
    public void onExtract(FurnaceExtractEvent event){
        //TODO: CANCEL EVENT

        System.out.println("Took out: "+event.getPlayer().getItemOnCursor());

    }

    @EventHandler
    public void onMoveItem(InventoryMoveItemEvent event){
        if(event.getSource() instanceof FurnaceInventory){
            System.out.println("Took out item! "+event.getItem());
        }
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
