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

import java.util.*;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

public class AnvilListener implements Listener {

    private static final int DELAY = 3;
    private static final HashMap<UUID, AnvilData> preCraftedRecipes = new HashMap<>();
    private final HashSet<UUID> playerDelay = new HashSet<>();

    private final CustomCrafting customCrafting;

    public AnvilListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCheck(PrepareAnvilEvent event) {
        var player = (Player) event.getView().getPlayer();
        AnvilInventory inventory = event.getInventory();

        ItemStack inputLeft = inventory.getItem(0);
        if (ItemUtils.isAirOrNull(inputLeft)) return; // Might be null for the first event, so ignore them and continue if there is an item.

        ItemStack inputRight = inventory.getItem(1);
        if (ItemUtils.isAirOrNull(inputLeft) && ItemUtils.isAirOrNull(inputRight)) {
            event.setResult(null);
            return; // Maybe some first events are missing both items? No need to continue then.
        }
        // The Event is called multiple times, so we ignore the last few events.
        if (playerDelay.contains(player.getUniqueId())) return;
        playerDelay.add(player.getUniqueId());
        Bukkit.getScheduler().runTask(customCrafting, () -> playerDelay.remove(player.getUniqueId())); // Free player for the next event lifecycle.
        preCraftedRecipes.remove(player.getUniqueId());

        Conditions.Data data = Conditions.Data.of(player, player.getOpenInventory());
        if (inventory.getLocation() != null) {
            data.setBlock(inventory.getLocation().getBlock());
        }

        customCrafting.getRegistries().getRecipes().get(RecipeType.ANVIL).stream()
                .filter(customRecipeAnvil -> !customRecipeAnvil.isDisabled() && customRecipeAnvil.checkConditions(data))
                .map(recipe -> {
                    Optional<CustomItem> finalInputLeft = Optional.empty();
                    Optional<CustomItem> finalInputRight = Optional.empty();
                    if (recipe.hasInputLeft() && (finalInputLeft = recipe.getInputLeft().check(inputLeft, recipe.isCheckNBT())).isEmpty()) return null;
                    if (recipe.hasInputRight() && (finalInputRight = recipe.getInputRight().check(inputRight, recipe.isCheckNBT())).isEmpty()) return null;
                    //Recipe is valid at this point!
                    return new AnvilData(recipe, new IngredientData[]{
                            new IngredientData(0, 0, recipe.getInputLeft(), finalInputLeft.orElse(null), inputLeft),
                            new IngredientData(1, 1, recipe.getInputRight(), finalInputRight.orElse(null), inputRight)}
                    );
                })
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(anvilData -> {
                    CustomRecipeAnvil recipe = anvilData.getRecipe();
                    //Set the result depending on what is configured!
                    final CustomItem resultItem = recipe.getRepairTask().computeResult(recipe, event, anvilData, player, inputLeft, inputRight);
                    int repairCost = Math.max(1, recipe.getRepairCost());
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
                    // Save current active recipe to consume correct item inputs!
                    preCraftedRecipes.put(player.getUniqueId(), anvilData);
                    final ItemStack finalResult = recipe.getResult().getItem(anvilData, resultItem, player, null);
                    event.setResult(repairCost > 0 ? finalResult : null);
                    inventory.setRepairCost(repairCost);
                    int finalRepairCost = repairCost;
                    Bukkit.getScheduler().runTask(customCrafting, () -> {
                        event.setResult(finalResult);
                        inventory.setItem(2, finalResult);
                        inventory.setRepairCost(finalRepairCost);
                        player.updateInventory();
                    });
                    player.updateInventory();
                });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
