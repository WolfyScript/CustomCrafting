package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPrepareAnvilEvent;
import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class AnvilListener implements Listener {

    @EventHandler
    public void onCheck(PrepareAnvilEvent event) {
        Player player = (Player) event.getView().getPlayer();
        AnvilInventory inventory = event.getInventory();
        List<CustomAnvilRecipe> recipes = CustomCrafting.getRecipeHandler().getAnvilRecipes();

        for (CustomAnvilRecipe recipe : recipes) {
            CustomPrepareAnvilEvent anvilEvent = new CustomPrepareAnvilEvent(event.getView(), event.getResult(), recipe);
            boolean left = true;
            if (recipe.hasInputLeft()) {
                if (inventory.getItem(0) != null) {
                    for (CustomItem customItem : recipe.getInputLeft().keySet()) {
                        if (!customItem.isSimilar(inventory.getItem(0), recipe.isExactMeta())) {
                            left = false;
                        } else {
                            left = true;
                            break;
                        }
                    }
                } else {
                    left = false;
                }
            }
            boolean right = true;
            if (recipe.hasInputRight() && left) {
                if (inventory.getItem(1) != null) {
                    for (CustomItem customItem : recipe.getInputRight().keySet()) {
                        if (!customItem.isSimilar(inventory.getItem(1), recipe.isExactMeta())) {
                            left = false;
                        } else {
                            left = true;
                            break;
                        }
                    }
                } else {
                    right = false;
                }
            }

            if (!left || !right) {
                anvilEvent.setCancelled(true);
            }
            Bukkit.getPluginManager().callEvent(anvilEvent);
            if (anvilEvent.isCancelled()) {
                continue;
            }
            //RECIPE RESULTS!
            inventory.setRepairCost(recipe.getRepairCost());

            if (recipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                inventory.setItem(2, recipe.getCustomResult());
            }else{
                ItemStack inputLeft = inventory.getItem(0);
                ItemStack result = inventory.getItem(2);
                if(recipe.isBlockEnchant()){
                    if(result.hasItemMeta() && result.getItemMeta().hasEnchants()){
                        result.getItemMeta().getEnchants().clear();
                        if(inputLeft.hasItemMeta() && inputLeft.getItemMeta().hasEnchants()){
                            result.addEnchantments(inputLeft.getEnchantments());
                        }
                    }
                }
                if(recipe.isBlockRename()){
                    ItemMeta itemMeta = result.getItemMeta();
                    if(inputLeft.hasItemMeta() && inputLeft.getItemMeta().hasDisplayName()){
                        itemMeta.setDisplayName(inputLeft.getItemMeta().getDisplayName());
                    }else{
                        itemMeta.setDisplayName(null);
                    }
                }
                if(recipe.isBlockRepair()){
                    if(result instanceof Damageable){
                        if(inputLeft instanceof Damageable){
                            ((Damageable) result).setDamage(((Damageable) inputLeft).getDamage());
                        }
                    }
                }
                inventory.setItem(2, result);
            }
        }
    }
}
