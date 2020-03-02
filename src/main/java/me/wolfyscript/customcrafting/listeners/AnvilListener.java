package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.ItemUtils;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AnvilListener implements Listener {

    @EventHandler
    public void onCheck(PrepareAnvilEvent event) {
        Player player = (Player) event.getView().getPlayer();
        AnvilInventory inventory = event.getInventory();
        List<CustomAnvilRecipe> recipes = CustomCrafting.getRecipeHandler().getAvailableAnvilRecipes(player);
        recipes.sort(Comparator.comparing(CustomRecipe::getPriority));
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
                            for(Map.Entry<Enchantment, Integer> entry : inputLeft.getEnchantments().entrySet()){
                                result.addUnsafeEnchantment(entry.getKey(), entry.getValue());
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
                        ItemMeta itemMeta = result.getItemMeta();
                        if (itemMeta instanceof Damageable) {
                            if (inputLeft.hasItemMeta() && inputLeft.getItemMeta() instanceof Damageable) {
                                ((Damageable) itemMeta).setDamage(((Damageable) inputLeft.getItemMeta()).getDamage());
                            }
                            result.setItemMeta(itemMeta);
                        }
                    }
                }
                if (result == null || result.getType().equals(Material.AIR)) {
                    result = inputLeft.clone();
                }
                if (recipe.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
                    if (WolfyUtilities.hasVillagePillageUpdate() && CustomItem.hasCustomDurability(result)) {
                        int damage = CustomItem.getCustomDamage(result) - recipe.getDurability();
                        if (damage < 0) {
                            damage = 0;
                        }
                        CustomItem.setCustomDamage(result, damage);
                    } else if (ItemUtils.hasCustomDurability(result)) {
                        int damage = ItemUtils.getDamage(result) - recipe.getDurability();
                        if (damage >= 0) {
                            damage = 0;
                        }
                        ItemUtils.setDamage(result, damage);
                    } else if (result.getItemMeta() instanceof Damageable) {
                        ItemMeta itemMeta = result.getItemMeta();
                        ((Damageable) itemMeta).setDamage(((Damageable) itemMeta).getDamage() - recipe.getDurability());
                        result.setItemMeta(itemMeta);
                    }
                }
            }
            int repairCost = recipe.getRepairCost();

            ItemMeta inputMeta = inputLeft.getItemMeta();
            if (inputMeta instanceof Repairable) {
                int itemRepairCost = ((Repairable) inputMeta).getRepairCost();
                if (recipe.getRepairCostMode().equals(CustomAnvilRecipe.RepairCostMode.ADD)) {
                    repairCost = repairCost + itemRepairCost;
                } else if (recipe.getRepairCostMode().equals(CustomAnvilRecipe.RepairCostMode.MULTIPLY)) {
                    repairCost = recipe.getRepairCost() * (itemRepairCost > 0 ? itemRepairCost : 1);
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
            Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                inventory.setRepairCost(finalRepairCost);
                event.setResult(finalResult);
                player.updateInventory();
            });
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof AnvilInventory) {
            AnvilInventory inventory = (AnvilInventory) event.getClickedInventory();
            //TODO: Input consume method
        }
    }
}
