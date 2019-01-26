package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.ShapedCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;

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
                ShapedCraftRecipe recipe = CustomCrafting.getRecipeHandler().getShapedRecipe(((ShapedRecipe) e.getRecipe()).getKey().toString());
                if(recipe != null){
                    //TODO: Check for Permission and Workbench!
                    List<ShapedCraftRecipe> group = CustomCrafting.getRecipeHandler().getShapedRecipeGroup(recipe);
                    if(!recipe.check(e.getInventory().getMatrix())){
                        ShapedCraftRecipe craftRecipe = getAllowedGroupShapedRecipe(group, e.getInventory().getMatrix());
                        if(craftRecipe != null){
                            //Custom Recipe allowed!
                            e.getInventory().setResult(craftRecipe.getResult());
                        }else{
                            e.getInventory().setResult(new ItemStack(Material.AIR));
                        }
                    }else{
                        //Custom Recipe allowed!
                    }
                }
            }else if(e.getRecipe() instanceof ShapelessRecipe){

            }

        }
    }

    private ShapedCraftRecipe getAllowedGroupShapedRecipe(List<ShapedCraftRecipe> group, ItemStack[] matrix){
        if(!group.isEmpty()){
            ShapedCraftRecipe allowedRecipe = null;
            for(ShapedCraftRecipe groupRecipe : group){
                if(groupRecipe.check(matrix)){
                    return groupRecipe;
                }
            }
        }
        return null;
    }


}
