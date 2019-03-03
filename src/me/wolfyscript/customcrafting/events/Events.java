package me.wolfyscript.customcrafting.events;

import jdk.nashorn.internal.ir.Block;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.*;
import org.bukkit.util.BoundingBox;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Events implements Listener {

    private WolfyUtilities api;

    public Events(WolfyUtilities api) {
        this.api = api;
    }

    @EventHandler
    public void onDiscover(PlayerRecipeDiscoverEvent event){
        if(CustomCrafting.getConfigHandler().getConfig().getVanillaRecipes().contains(event.getRecipe().toString())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPreCraftNew(PrepareItemCraftEvent e) {
        if (e.getRecipe() != null) {
            if(e.getRecipe() instanceof Keyed && CustomCrafting.getConfigHandler().getConfig().getVanillaRecipes().contains(((Keyed) e.getRecipe()).getKey().toString())){
                e.getInventory().setResult(new ItemStack(Material.AIR));
            }
            CustomRecipe customRecipe = CustomCrafting.getRecipeHandler().getRecipe(e.getRecipe());
            CraftingRecipe recipe = customRecipe instanceof CraftingRecipe ? (CraftingRecipe) customRecipe : null;
            Player player = (Player) e.getView().getPlayer();
            if (recipe != null) {
                CustomCraftEvent customCraftEvent = null;
                try {
                    List<CustomRecipe> group = CustomCrafting.getRecipeHandler().getRecipeGroup(recipe);
                    if (recipe.check(e.getInventory().getMatrix()) && checkWorkbenchAndPerm(player, e.getView().getPlayer().getTargetBlock(null, 5).getLocation(), recipe)) {
                        customCraftEvent = new CustomCraftEvent(e.isRepair(), recipe, e.getRecipe(), e.getInventory());
                    } else {
                        CraftingRecipe craftRecipe = getAllowedRecipe(group, e.getInventory().getMatrix());
                        if (craftRecipe != null) {
                            customCraftEvent = new CustomCraftEvent(e.isRepair(), craftRecipe, e.getRecipe(), e.getInventory());
                            if (!checkWorkbenchAndPerm(player, e.getView().getPlayer().getTargetBlock(null, 5).getLocation(), craftRecipe)) {
                                customCraftEvent.setCancelled(true);
                            }
                        }
                    }
                    if (customCraftEvent != null && !customCraftEvent.isCancelled()) {
                        //ALLOWED
                        e.getInventory().setResult(customCraftEvent.getResult());
                    } else {
                        //DENIED
                        e.getInventory().setResult(new ItemStack(Material.AIR));
                        if(CustomCrafting.getConfigHandler().getConfig().getCommandsDeniedCraft() != null && !CustomCrafting.getConfigHandler().getConfig().getCommandsDeniedCraft().isEmpty()){
                            for(String command : CustomCrafting.getConfigHandler().getConfig().getCommandsDeniedCraft()){
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%P%", player.getName()).replace("%UUID%", player.getUniqueId().toString()));
                            }
                        }
                    }

                    CustomCrafting.getWorkbenches().setContents(e.getView().getPlayer().getTargetBlock(null, 5).getLocation(), e.getInventory().getMatrix());

                } catch (Exception ex) {
                    System.out.println("WHAT HAPPENED? Please report!");
                    ex.printStackTrace();
                    System.out.println("WHAT HAPPENED? Please report!");
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                }
            }
        }
    }

    private boolean checkWorkbenchAndPerm(Player player, Location location, CraftingRecipe recipe) {
        if (!recipe.needsAdvancedWorkbench() || (location != null && CustomCrafting.getWorkbenches().isWorkbench(location))) {
            return !recipe.needsPermission() || (player.hasPermission("customcrafting.craft.*") || player.hasPermission("customcrafting.craft." + recipe.getID()) || player.hasPermission("customcrafting.craft." + recipe.getID().split(":")[0]));
        }
        return false;
    }

    private CraftingRecipe getAllowedRecipe(List<CustomRecipe> group, ItemStack[] matrix) {
        if (!group.isEmpty()) {
            for (CustomRecipe groupRecipe : group) {
                if (groupRecipe instanceof CraftingRecipe && ((CraftingRecipe) groupRecipe).check(matrix)) {
                    return (CraftingRecipe) groupRecipe;
                }
            }
        }
        return null;
    }



}
