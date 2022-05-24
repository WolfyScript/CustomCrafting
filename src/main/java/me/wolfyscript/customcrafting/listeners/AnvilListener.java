/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.data.AnvilData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AnvilListener implements Listener {

    private static final HashMap<UUID, AnvilData> preCraftedRecipes = new HashMap<>();

    private final CustomCrafting customCrafting;

    public AnvilListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onCheck(PrepareAnvilEvent event) {
        var player = (Player) event.getView().getPlayer();
        AnvilInventory inventory = event.getInventory();
        preCraftedRecipes.remove(player.getUniqueId());
        ItemStack inputLeft = inventory.getItem(0);
        ItemStack inputRight = inventory.getItem(1);
        if (ItemUtils.isAirOrNull(inputLeft) && ItemUtils.isAirOrNull(inputRight)) {
            event.setResult(null);
            return;
        }
        for (CustomRecipeAnvil recipe : customCrafting.getRegistries().getRecipes().getAvailable(RecipeType.ANVIL, player)) {
            Optional<CustomItem> finalInputLeft = Optional.empty();
            Optional<CustomItem> finalInputRight = Optional.empty();
            if ((recipe.hasInputLeft() && (inputLeft == null || (finalInputLeft = recipe.getInputLeft().check(inputLeft, recipe.isCheckNBT())).isEmpty()))
                    || (recipe.hasInputRight() && (inputRight == null || (finalInputRight = recipe.getInputRight().check(inputRight, recipe.isCheckNBT())).isEmpty()))) {
                continue;
            }
            //Recipe is valid at this point!
            AnvilData anvilData = new AnvilData(recipe, Map.of(
                    0, new IngredientData(0, recipe.getInputLeft(), finalInputLeft.orElse(null), inputLeft),
                    1, new IngredientData(1, recipe.getInputRight(), finalInputRight.orElse(null), inputRight))
            );
            //Set the result depending on what is configured!
            final CustomItem resultItem = recipe.getRepairTask().computeResult(recipe, event, anvilData, player, inputLeft, inputRight);
            int repairCost = Math.max(1, recipe.getRepairCost());
            if (inputLeft != null) {
                var inputMeta = inputLeft.getItemMeta();
                //Configure the Repair cost
                if (inputMeta instanceof Repairable repairable) {
                    int itemRepairCost = repairable.getRepairCost();
                    if (recipe.getRepairCostMode().equals(CustomRecipeAnvil.RepairCostMode.ADD)) {
                        repairCost = repairCost + itemRepairCost;
                    } else if (recipe.getRepairCostMode().equals(CustomRecipeAnvil.RepairCostMode.MULTIPLY)) {
                        repairCost = recipe.getRepairCost() * (itemRepairCost > 0 ? itemRepairCost : 1);
                    }
                }
                //Apply the repair cost to the result.
                if (recipe.isApplyRepairCost()) {
                    var itemMeta = resultItem.getItemMeta();
                    if (itemMeta instanceof Repairable repairable) {
                        repairable.setRepairCost(repairCost);
                        resultItem.setItemMeta(itemMeta);
                    }
                }
            }
            //Save current active recipe to consume correct item inputs!
            preCraftedRecipes.put(player.getUniqueId(), anvilData);
            final ItemStack finalResult = recipe.getResult().getItem(anvilData, resultItem, player, null);
            inventory.setRepairCost(repairCost);
            event.setResult(repairCost > 0 ? finalResult : null);
            player.updateInventory();
            int finalRepairCost = repairCost;
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                inventory.setRepairCost(finalRepairCost);
                player.updateInventory();
            });
            break;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof AnvilInventory inventory) {
            var player = (Player) event.getWhoClicked();
            if (event.getSlot() == 2 && !ItemUtils.isAirOrNull(event.getCurrentItem()) && preCraftedRecipes.get(player.getUniqueId()) != null) {
                event.setCancelled(true);
                var anvilData = preCraftedRecipes.get(player.getUniqueId());
                if (inventory.getRepairCost() > 0 && (player.getLevel() >= inventory.getRepairCost() || player.getGameMode() == GameMode.CREATIVE)) {
                    ItemStack result = event.getCurrentItem();
                    ItemStack cursor = event.getCursor();
                    if (event.isShiftClick()) {
                        if (!InventoryUtils.hasInventorySpace(player, result)) return;
                        player.getInventory().addItem(result);
                    } else if (ItemUtils.isAirOrNull(cursor) || (result.isSimilar(cursor) && cursor.getAmount() + result.getAmount() <= cursor.getMaxStackSize())) {
                        if (ItemUtils.isAirOrNull(cursor)) {
                            event.setCursor(result);
                        } else {
                            cursor.setAmount(cursor.getAmount() + result.getAmount());
                        }
                    } else {
                        return;
                    }
                    if (anvilData.isUsedResult()) {
                        Result recipeResult = anvilData.getResult();
                        recipeResult.executeExtensions(inventory.getLocation() != null ? inventory.getLocation() : player.getLocation(), inventory.getLocation() != null, player);
                        recipeResult.removeCachedItem(player);
                    }
                    preCraftedRecipes.remove(player.getUniqueId());

                    if (inventory.getLocation() != null && inventory.getLocation().getWorld() != null) {
                        //Play sound & TODO: damage the Anvil Block!
                        var location = inventory.getLocation();
                        location.getWorld().playEffect(location, Effect.ANVIL_USE, 0);
                    }
                    if (player.getLevel() >= inventory.getRepairCost()) {
                        player.setLevel(player.getLevel() - inventory.getRepairCost());
                    }
                    event.setCurrentItem(null);
                    player.updateInventory();

                    IngredientData inputLeft = anvilData.getLeftIngredient();
                    IngredientData inputRight = anvilData.getRightIngredient();
                    consumeInputItem(inventory, inputLeft.customItem(), inputLeft, 0);
                    consumeInputItem(inventory, inputRight.customItem(), inputRight, 1);
                }
            }
        }
    }

    private void consumeInputItem(AnvilInventory inventory, CustomItem input, IngredientData ingredient, int slot) {
        if (input != null && inventory.getItem(slot) != null) {
            ItemStack item = ingredient.itemStack().clone();
            input.remove(item, 1, inventory);
            inventory.setItem(slot, item);
        } else {
            inventory.setItem(slot, null);
        }
    }
}
