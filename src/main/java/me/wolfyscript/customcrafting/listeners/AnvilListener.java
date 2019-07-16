package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPrepareAnvilEvent;
import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.List;

public class AnvilListener implements Listener {

    @EventHandler
    public void onCheck(PrepareAnvilEvent event) {
        Player player = (Player) event.getView().getPlayer();
        AnvilInventory inventory = event.getInventory();
        List<CustomAnvilRecipe> recipes = CustomCrafting.getRecipeHandler().getAnvilRecipes();

        for (CustomAnvilRecipe recipe : recipes) {
            if (recipe.hasInputLeft()) {
                boolean left = false;
                if (inventory.getItem(0) != null) {
                    for (CustomItem customItem : recipe.getInputLeft()) {
                        if (customItem.isSimilar(inventory.getItem(0), recipe.isExactMeta())) {
                            left = true;
                            break;
                        }
                    }
                    if (!left) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            if (recipe.hasInputRight()) {
                boolean right = false;
                if (inventory.getItem(1) != null) {
                    for (CustomItem customItem : recipe.getInputRight()) {
                        if (customItem.isSimilar(inventory.getItem(1), recipe.isExactMeta())) {
                            right = true;
                            break;
                        }
                    }
                    if (!right) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            ItemStack inputLeft = inventory.getItem(0);
            ItemStack result;
            //RECIPE RESULTS!
            if (recipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                result = recipe.getCustomResult();
            } else {
                result = event.getResult();
                if (result != null && result.hasItemMeta()) {
                    if (recipe.isBlockEnchant()) {
                        if (result.hasItemMeta() && result.getItemMeta().hasEnchants()) {
                            for (Enchantment enchantment : result.getEnchantments().keySet()) {
                                result.removeEnchantment(enchantment);
                            }
                            if (inputLeft.hasItemMeta() && inputLeft.getItemMeta().hasEnchants()) {
                                result.addUnsafeEnchantments(inputLeft.getEnchantments());
                            }
                        }
                    }
                    if (recipe.isBlockRename()) {
                        ItemMeta itemMeta = result.getItemMeta();
                        if (inputLeft.hasItemMeta() && inputLeft.getItemMeta().hasDisplayName()) {
                            itemMeta.setDisplayName(inputLeft.getItemMeta().getDisplayName());
                        } else {
                            itemMeta.setDisplayName(null);
                        }
                        result.setItemMeta(itemMeta);
                    }
                    if (recipe.isBlockRepair()) {
                        if (result instanceof Damageable) {
                            if (inputLeft instanceof Damageable) {
                                ((Damageable) result).setDamage(((Damageable) inputLeft).getDamage());
                            }
                        }
                    }
                }
                if (recipe.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
                    if (result instanceof Damageable) {
                        ((Damageable) result).setDamage(((Damageable) result).getDamage() + recipe.getDurability());
                    }
                }
            }

            int repairCost = recipe.getRepairCost();

            ItemMeta inputMeta = inputLeft.getItemMeta();
            if (inputMeta instanceof Repairable) {
                if (recipe.getRepairCostMode().equals(CustomAnvilRecipe.RepairCostMode.ADD)) {
                    repairCost = repairCost + recipe.getRepairCost();
                } else if (recipe.getRepairCostMode().equals(CustomAnvilRecipe.RepairCostMode.MULTIPLY)) {
                    repairCost = ((repairCost > 0 ? repairCost : 1) * recipe.getRepairCost());
                }
            }

            if (recipe.isApplyRepairCost()) {
                ItemMeta itemMeta = result.getItemMeta();
                if (itemMeta instanceof Repairable) {
                    ((Repairable) itemMeta).setRepairCost(repairCost);
                    result.setItemMeta(itemMeta);
                }
            }

            /*
                 Set the values and result 1 tick after they are replaced by NMS.
                 So the player will get the correct Item and the correct values are displayed!
            */
            final int finalRepairCost = repairCost;
            ItemStack finalResult = result;
            inventory.setRepairCost(finalRepairCost);
            event.setResult(finalResult);
            Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                inventory.setRepairCost(finalRepairCost);
                event.setResult(finalResult);
                player.updateInventory();
            }, 1);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof AnvilInventory) {
            AnvilInventory inventory = (AnvilInventory) event.getClickedInventory();
            System.out.print("Took item: " + inventory.getItem(2));
            System.out.print("c: " + event.isCancelled());
            //TODO: Input consume method
        }


    }
}
