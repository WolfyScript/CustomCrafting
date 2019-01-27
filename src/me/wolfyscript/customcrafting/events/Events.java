package me.wolfyscript.customcrafting.events;

import jdk.nashorn.internal.ir.Block;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.FurnaceCRecipe;
import me.wolfyscript.customcrafting.recipes.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Events implements Listener {

    private WolfyUtilities api;

    public Events(WolfyUtilities api) {
        this.api = api;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            ItemStack itemStack = event.getItemInHand();
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                String name = itemStack.getItemMeta().getDisplayName();
                if (name.contains(":")) {
                    name = WolfyUtilities.unhideString(name);
                    String verify = name.split(":")[1];
                    if (verify.equals("customcrafting")) {
                        CustomCrafting.getWorkbenches().addWorkbench(event.getBlockPlaced().getLocation());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPreCraftNew(PrepareItemCraftEvent e) {
        if (e.getRecipe() != null) {
            CraftingRecipe recipe = CustomCrafting.getRecipeHandler().getRecipe(e.getRecipe());
            if (recipe != null) {
                CustomCraftEvent customCraftEvent = null;
                try {
                    List<CraftingRecipe> group = CustomCrafting.getRecipeHandler().getRecipeGroup(recipe);
                    if (recipe.check(e.getInventory().getMatrix()) && checkWorkbenchAndPerm((Player) e.getView().getPlayer(), e.getView().getPlayer().getTargetBlock(null, 5).getLocation(), recipe)) {
                        customCraftEvent = new CustomCraftEvent(e.isRepair(), recipe, e.getRecipe(), e.getInventory());
                    } else {
                        CraftingRecipe craftRecipe = getAllowedRecipe(group, e.getInventory().getMatrix());
                        if (craftRecipe != null) {
                            customCraftEvent = new CustomCraftEvent(e.isRepair(), craftRecipe, e.getRecipe(), e.getInventory());
                            if (!checkWorkbenchAndPerm((Player) e.getView().getPlayer(), e.getView().getPlayer().getTargetBlock(null, 5).getLocation(), craftRecipe)) {
                                customCraftEvent.setCancelled(true);
                            }
                        }
                    }
                    if (customCraftEvent != null && !customCraftEvent.isCancelled()) {
                        e.getInventory().setResult(customCraftEvent.getResult());
                    } else {
                        e.getInventory().setResult(new ItemStack(Material.AIR));
                    }
                } catch (Exception ex) {
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                }
            }
        }
    }

    private boolean checkWorkbenchAndPerm(Player player, Location location, CraftingRecipe recipe) {
        System.out.println("loc: " + location);
        if (!recipe.needsAdvancedWorkbench() || (location != null && CustomCrafting.getWorkbenches().isWorkbench(location))) {
            System.out.println("Workbench correct!");
            return !recipe.needsPermission() || (player.hasPermission("customcrafting.craft.*") || player.hasPermission("customcrafting.craft." + recipe.getID()) || player.hasPermission("customcrafting.craft." + recipe.getID().split(":")[0]));
        }
        return false;
    }

    @EventHandler
    public void onSmelting(FurnaceSmeltEvent event) {
        FurnaceCRecipe recipe = CustomCrafting.getRecipeHandler().getFurnaceRecipe(event.getSource());
        if (recipe != null) {
            if (!recipe.check(event.getSource())) {
                FurnaceCRecipe furnaceRecipe = getAllowedFurnaceRecipe(CustomCrafting.getRecipeHandler().getFurnaceRecipes(event.getSource()), event.getSource());
                if (furnaceRecipe != null) {
                    //Recipe Allowed
                    event.setResult(furnaceRecipe.getResult());
                } else {
                    event.setCancelled(true);
                }
            } else {
                //Recipe Allowed
                event.setResult(recipe.getResult());
            }
        }
    }


    private CraftingRecipe getAllowedRecipe(List<CraftingRecipe> group, ItemStack[] matrix) {
        if (!group.isEmpty()) {
            for (CraftingRecipe groupRecipe : group) {
                if (groupRecipe.check(matrix)) {
                    return groupRecipe;
                }
            }
        }
        return null;
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
