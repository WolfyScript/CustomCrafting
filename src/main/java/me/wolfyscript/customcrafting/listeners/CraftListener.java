package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.RecipeUtils;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {

    private WolfyUtilities api;

    private MainConfig config = CustomCrafting.getConfigHandler().getConfig();

    public CraftListener(WolfyUtilities api) {
        this.api = api;
    }

    @EventHandler
    public void onAdvancedWorkbench(CustomPreCraftEvent event) {
        if (!event.isCancelled() && event.getRecipe().getId().equals("customcrafting:workbench")) {
            if (!CustomCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof CraftingInventory && event.getSlot() == 0) {
            CraftingInventory inventory = (CraftingInventory) event.getClickedInventory();
            ItemStack resultItem = inventory.getResult();
            inventory.setResult(new ItemStack(Material.AIR));
            ItemStack[] matrix = inventory.getMatrix().clone();
            RecipeUtils.consumeRecipe(resultItem, matrix, event);

            Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                inventory.setMatrix(matrix);

            });
            RecipeUtils.getPrecraftedRecipes().put(event.getWhoClicked().getUniqueId(), null);
        } else if (event.getClickedInventory() instanceof CraftingInventory) {
            Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                PrepareItemCraftEvent event1 = new PrepareItemCraftEvent((CraftingInventory) event.getClickedInventory(), event.getView(), false);
                Bukkit.getPluginManager().callEvent(event1);
            }, 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreCraft(PrepareItemCraftEvent e) {
        Player player = (Player) e.getView().getPlayer();
        try {
            RecipeHandler recipeHandler = CustomCrafting.getRecipeHandler();
            ItemStack[] matrix = e.getInventory().getMatrix();
            ItemStack result = RecipeUtils.preCheckRecipe(matrix, player, e.isRepair(), e.getInventory());
            if (result != null) {
                e.getInventory().setResult(result);
            } else {
                api.sendDebugMessage("No valid recipe!");
                RecipeUtils.getPrecraftedRecipes().remove(player.getUniqueId());
                if (e.getRecipe() != null) {
                    if (e.getRecipe() instanceof Keyed) {
                        api.sendDebugMessage("Detected recipe: " + ((Keyed) e.getRecipe()).getKey());
                        CraftingRecipe recipe = recipeHandler.getAdvancedCraftingRecipe(((Keyed) e.getRecipe()).getKey().toString());
                        if (recipeHandler.getDisabledRecipes().contains(((Keyed) e.getRecipe()).getKey().toString()) || recipe != null) {
                            //Recipe is disabled or it is a custom recipe!
                            e.getInventory().setResult(new ItemStack(Material.AIR));
                        } else {
                            api.sendDebugMessage("Use vanilla recipe output!");
                        }
                    }
                }
            }
            player.updateInventory();
        } catch (Exception ex) {
            System.out.println("WHAT HAPPENED? Please report!");
            ex.printStackTrace();
            System.out.println("WHAT HAPPENED? Please report!");
            RecipeUtils.getPrecraftedRecipes().remove(player.getUniqueId());
            e.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

}
