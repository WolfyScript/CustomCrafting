package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.anvil.AnvilData;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
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

import java.util.*;

public class AnvilListener implements Listener {

    private static final HashMap<UUID, AnvilData> preCraftedRecipes = new HashMap<>();

    private final CustomCrafting customCrafting;

    public AnvilListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onCheck(PrepareAnvilEvent event) {
        Player player = (Player) event.getView().getPlayer();
        AnvilInventory inventory = event.getInventory();
        List<CustomAnvilRecipe> recipes = Registry.RECIPES.getAvailable(Types.ANVIL, player);
        recipes.sort(Comparator.comparing(ICustomRecipe::getPriority));
        preCraftedRecipes.remove(player.getUniqueId());
        ItemStack inputLeft = inventory.getItem(0);
        ItemStack inputRight = inventory.getItem(1);
        if (ItemUtils.isAirOrNull(inputLeft) && ItemUtils.isAirOrNull(inputRight)) {
            event.setResult(null);
            return;
        }
        for (CustomAnvilRecipe recipe : recipes) {
            Optional<CustomItem> finalInputLeft = Optional.empty();
            Optional<CustomItem> finalInputRight = Optional.empty();
            if (recipe.hasInputLeft()) {
                if (inputLeft != null) {
                    finalInputLeft = recipe.getInputLeft().check(inputLeft, recipe.isExactMeta());
                    if (!finalInputLeft.isPresent()) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            if (recipe.hasInputRight()) {
                if (inputRight != null) {
                    finalInputRight = recipe.getInputRight().check(inputRight, recipe.isExactMeta());
                    if (!finalInputRight.isPresent()) {
                        continue;
                    }
                } else {
                    continue;
                }
            }

            //Recipe is valid at this point!
            final CustomItem result;
            Result<?> recipeResult = null;
            //Set the result depending on what is configured!
            if (recipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                //Recipe has a plain result set that we can use.
                recipeResult = recipe.getResult().get(new ItemStack[]{inputLeft, inventory.getItem(1)});
                result = recipeResult.getItem(player).orElse(new CustomItem(Material.AIR));
            } else if (!ItemUtils.isAirOrNull(event.getResult())) {
                //Either none or durability mode is set.
                result = new CustomItem(event.getResult());
                ItemStack resultStack = result.create();
                if (resultStack.hasItemMeta()) {
                    //Further recipe options to block features.
                    if (recipe.isBlockEnchant() && resultStack.hasItemMeta() && result.getItemMeta().hasEnchants()) {
                        //Block Enchants
                        for (Enchantment enchantment : resultStack.getEnchantments().keySet()) {
                            result.removeEnchantment(enchantment);
                        }
                        if (inputLeft != null) {
                            for (Map.Entry<Enchantment, Integer> entry : inputLeft.getEnchantments().entrySet()) {
                                result.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    if (recipe.isBlockRename()) {
                        //Block Renaming
                        ItemMeta itemMeta = result.getItemMeta();
                        if (inputLeft != null && inputLeft.hasItemMeta() && inputLeft.getItemMeta().hasDisplayName()) {
                            itemMeta.setDisplayName(inputLeft.getItemMeta().getDisplayName());
                        } else {
                            itemMeta.setDisplayName(null);
                        }
                        result.setItemMeta(itemMeta);
                    }
                    if (recipe.isBlockRepair()) {
                        //Block Repairing
                        ItemMeta itemMeta = result.getItemMeta();
                        if (itemMeta instanceof Damageable) {
                            if (inputLeft != null && inputLeft.hasItemMeta() && inputLeft.getItemMeta() instanceof Damageable) {
                                ((Damageable) itemMeta).setDamage(((Damageable) inputLeft.getItemMeta()).getDamage());
                            }
                            result.setItemMeta(itemMeta);
                        }
                    }
                }
                if (recipe.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
                    //Durability mode is set.
                    if (result.hasCustomDurability()) {
                        int damage = result.getCustomDamage() - recipe.getDurability();
                        if (damage < 0) {
                            damage = 0;
                        }
                        result.setCustomDamage(damage);
                    } else if (result.getItemMeta() instanceof Damageable) {
                        ItemMeta itemMeta = result.getItemMeta();
                        ((Damageable) itemMeta).setDamage(((Damageable) itemMeta).getDamage() - recipe.getDurability());
                        result.setItemMeta(itemMeta);
                    }
                }
            } else {
                event.setResult(null);
                continue;
            }
            int repairCost = Math.max(1, recipe.getRepairCost());

            if (inputLeft != null) {
                ItemMeta inputMeta = inputLeft.getItemMeta();
                //Configure the Repair cost
                if (inputMeta instanceof Repairable) {
                    int itemRepairCost = ((Repairable) inputMeta).getRepairCost();
                    if (recipe.getRepairCostMode().equals(CustomAnvilRecipe.RepairCostMode.ADD)) {
                        repairCost = repairCost + itemRepairCost;
                    } else if (recipe.getRepairCostMode().equals(CustomAnvilRecipe.RepairCostMode.MULTIPLY)) {
                        repairCost = recipe.getRepairCost() * (itemRepairCost > 0 ? itemRepairCost : 1);
                    }
                }
                //Apply the repair cost to the result.
                if (recipe.isApplyRepairCost()) {
                    ItemMeta itemMeta = result.getItemMeta();
                    if (itemMeta instanceof Repairable) {
                        ((Repairable) itemMeta).setRepairCost(repairCost);
                        result.setItemMeta(itemMeta);
                    }
                }
            }

            //Save current active recipe to consume correct item inputs!
            preCraftedRecipes.put(player.getUniqueId(), new AnvilData(recipe, recipeResult, finalInputLeft.orElse(null), finalInputRight.orElse(null)));
            final ItemStack finalResult = result.create();
            inventory.setRepairCost(repairCost);
            event.setResult(repairCost > 0 ? finalResult : null);
            player.updateInventory();
            int finalRepairCost = repairCost;
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                if (inventory.getRepairCost() == 0) {
                    inventory.setRepairCost(finalRepairCost);
                }
                player.updateInventory();
            });
            break;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof AnvilInventory inventory) {
            Player player = (Player) event.getWhoClicked();
            if (event.getSlot() == 2 && !ItemUtils.isAirOrNull(event.getCurrentItem())) {
                if (preCraftedRecipes.get(player.getUniqueId()) != null) {
                    event.setCancelled(true);
                    AnvilData anvilData = preCraftedRecipes.get(player.getUniqueId());
                    if (inventory.getRepairCost() > 0 && player.getLevel() >= inventory.getRepairCost()) {
                        ItemStack result = event.getCurrentItem();
                        ItemStack cursor = event.getCursor();
                        if (event.isShiftClick()) {
                            if (InventoryUtils.hasInventorySpace(player, result)) {
                                player.getInventory().addItem(result);
                            } else {
                                return;
                            }
                        } else if (ItemUtils.isAirOrNull(cursor) || (result.isSimilar(cursor) && cursor.getAmount() + result.getAmount() <= cursor.getMaxStackSize())) {
                            if (ItemUtils.isAirOrNull(cursor)) {
                                event.setCursor(result);
                            } else {
                                cursor.setAmount(cursor.getAmount() + result.getAmount());
                            }
                        } else {
                            return;
                        }
                        if (anvilData.getResult().isPresent()) {
                            Result<?> recipeResult = anvilData.getResult().get();
                            recipeResult.executeExtensions(inventory.getLocation() != null ? inventory.getLocation() : player.getLocation(), inventory.getLocation() != null, player);
                            recipeResult.removeCachedItem(player);
                        }
                        preCraftedRecipes.remove(player.getUniqueId());

                        if (inventory.getLocation() != null && inventory.getLocation().getWorld() != null) {
                            //Play sound & TODO: damage the Anvil Block!
                            Location location = inventory.getLocation();
                            location.getWorld().playEffect(location, Effect.ANVIL_USE, 0);
                        }

                        event.setCurrentItem(null);
                        player.updateInventory();

                        CustomItem inputLeft = anvilData.getInputLeft();
                        CustomItem inputRight = anvilData.getInputRight();

                        if (inputLeft != null && inventory.getItem(0) != null) {
                            ItemStack itemLeft = inventory.getItem(0).clone();
                            inputLeft.consumeItem(itemLeft, itemLeft.getAmount(), inventory);
                            inventory.setItem(0, itemLeft);
                        } else {
                            inventory.setItem(0, null);
                        }
                        if (inputRight != null && inventory.getItem(1) != null) {
                            ItemStack itemRight = inventory.getItem(1).clone();
                            inputRight.consumeItem(itemRight, 1, inventory);
                            inventory.setItem(1, itemRight);
                        } else {
                            inventory.setItem(1, null);
                        }
                    }
                } else {
                    //Vanilla Recipe
                }
            }
        }
    }
}
