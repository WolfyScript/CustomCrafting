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
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.data.GrindstoneData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class GrindStoneListener implements Listener {

    private static final HashMap<UUID, GrindstoneData> preCraftedRecipes = new HashMap<>();
    private final CustomCrafting customCrafting;

    public GrindStoneListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onTakeOutResult(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getAction().equals(InventoryAction.NOTHING) || !event.getClickedInventory().getType().equals(InventoryType.GRINDSTONE))
            return;
        var player = (Player) event.getWhoClicked();
        var action = event.getAction();
        var inventory = event.getClickedInventory();
        if (event.getSlot() == 2 && !ItemUtils.isAirOrNull(inventory.getItem(2)) && (action.toString().startsWith("PICKUP_") || action.equals(InventoryAction.COLLECT_TO_CURSOR) || action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))) {
            //Take out item!
            if (preCraftedRecipes.get(player.getUniqueId()) == null) {
                //Vanilla Recipe
                return;
            }
            event.setCancelled(true);
            var grindstoneData = preCraftedRecipes.get(player.getUniqueId());

            //Result taken out and placed on cursor or into the inventory.
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
            } else return;
            if (grindstoneData.getRecipe().getXp() > 0) { //Spawn xp
                ExperienceOrb orb = (ExperienceOrb) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(grindstoneData.getRecipe().getXp());
            }
            grindstoneData.getResult().executeExtensions(inventory.getLocation() != null ? inventory.getLocation() : player.getLocation(), inventory.getLocation() != null, player);

            //Custom Recipe
            CustomItem inputTop = grindstoneData.getInputTop();
            CustomItem inputBottom = grindstoneData.getInputBottom();

            if (!ItemUtils.isAirOrNull(inputTop)) {
                ItemStack itemTop = inventory.getItem(0);
                if (!ItemUtils.isAirOrNull(itemTop)) {
                    inputTop.remove(itemTop, 1, inventory);
                }
            }
            if (!ItemUtils.isAirOrNull(inputBottom)) {
                ItemStack itemBottom = inventory.getItem(1);
                if (!ItemUtils.isAirOrNull(itemBottom)) {
                    inputBottom.remove(itemBottom, 1, inventory);
                }
            }
            //Remove crafted recipe.
            preCraftedRecipes.remove(player.getUniqueId());

            player.updateInventory();

            //Check for new recipe!
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                Pair<CustomItem, GrindstoneData> checkResult = checkRecipe(inventory.getItem(0), inventory.getItem(1), 0, player, event.getView());
                if (checkResult != null && checkResult.getValue().getRecipe() != null) {
                    preCraftedRecipes.put(player.getUniqueId(), checkResult.getValue());
                    inventory.setItem(2, checkResult.getKey().create());
                } else {
                    inventory.setItem(2, null);
                }
                player.updateInventory();
            });
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !event.getClickedInventory().getType().equals(InventoryType.GRINDSTONE))
            return;
        var player = (Player) event.getWhoClicked();
        var action = event.getAction();
        var inventory = event.getClickedInventory();
        if (event.getSlot() != 2) {
            //Place in items and click empty result slot
            final ItemStack cursor = event.getCursor(); //And the item in the cursor
            final ItemStack currentItem = event.getCurrentItem(); //We want to get the item in the slot

            if (event.getAction().toString().startsWith("PICKUP_") || action.equals(InventoryAction.COLLECT_TO_CURSOR) || action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                return;
            }
            ItemStack calculatedCursor = cursor;
            ItemStack calculatedCurrentItem = currentItem;
            //Place item when the item is valid
            event.setCancelled(true);
            if (event.isRightClick()) {
                //Dropping one item or pick up half
                if (action.equals(InventoryAction.PICKUP_HALF) || action.equals(InventoryAction.PICKUP_SOME)) return;
                //Dropping one item
                if (ItemUtils.isAirOrNull(currentItem)) {
                    calculatedCurrentItem = cursor.clone();
                    calculatedCurrentItem.setAmount(1);
                    calculatedCursor = cursor.clone();
                    calculatedCursor.setAmount(cursor.getAmount() - 1);
                } else if (currentItem.isSimilar(cursor) && currentItem.getAmount() < currentItem.getMaxStackSize() && cursor.getAmount() > 0) {
                    calculatedCurrentItem = currentItem.clone();
                    calculatedCurrentItem.setAmount(currentItem.getAmount() + 1);
                    calculatedCursor = cursor.clone();
                    calculatedCursor.setAmount(cursor.getAmount() - 1);
                }
            } else {
                //Placing an item
                if (ItemUtils.isAirOrNull(cursor)) return; //Make sure cursor contains item
                if (!ItemUtils.isAirOrNull(currentItem)) {
                    if (currentItem.isSimilar(cursor) || cursor.isSimilar(currentItem)) {
                        int possibleAmount = currentItem.getMaxStackSize() - currentItem.getAmount();
                        calculatedCurrentItem = currentItem.clone();
                        calculatedCurrentItem.setAmount(currentItem.getAmount() + (Math.min(cursor.getAmount(), possibleAmount)));
                        calculatedCursor = cursor.clone();
                        calculatedCursor.setAmount(cursor.getAmount() - possibleAmount);
                    } else if (!ItemUtils.isAirOrNull(cursor)) {
                        calculatedCursor = currentItem.clone();
                        calculatedCurrentItem = cursor.clone();
                    }
                } else {
                    calculatedCursor = null;
                    calculatedCurrentItem = cursor.clone();
                }
            }
            Pair<CustomItem, GrindstoneData> checkResult = checkRecipe(calculatedCurrentItem, inventory.getItem(event.getSlot() == 0 ? 1 : 0), event.getSlot(), player, event.getView());
            GrindstoneData data = null;
            if (checkResult != null) { //There exists a valid recipe with that ingredient
                data = checkResult.getValue();
                if (!canBePlacedIntoGrindstone(calculatedCurrentItem, inventory.getItem(event.getSlot() == 0 ? 1 : 0))) {
                    //Item cannot be placed into the inventory in vanilla. Using custom calculation.
                    event.setCurrentItem(calculatedCurrentItem);
                    event.getWhoClicked().setItemOnCursor(calculatedCursor);
                } else { //Use the vanilla item calculation instead.
                    event.setCancelled(false);
                }
                if (data == null) {
                    //The recipe isn't completed yet!
                    inventory.setItem(2, null);
                    return;
                }
                inventory.setItem(2, checkResult.getKey().create());
                player.updateInventory();
                Bukkit.getScheduler().runTask(customCrafting, player::updateInventory);
            } else {
                //No Recipe found! Using the vanilla behaviour
                customCrafting.getApi().getRegistries().getCustomItems().getByItemStack(calculatedCurrentItem).ifPresentOrElse(customItem -> {
                    if (customItem.isBlockVanillaRecipes()) event.setCancelled(true);
                }, () -> event.setCancelled(false));
            }
            preCraftedRecipes.put(player.getUniqueId(), data);
        }
    }

    private boolean canBePlacedIntoGrindstone(ItemStack itemStack, ItemStack other) {
        return !itemStack.getEnchantments().isEmpty() && (ItemUtils.isAirOrNull(other) || itemStack.isSimilar(other));
    }

    private boolean hasVanillaResult(ItemStack itemStack) {
        return itemStack.getAmount() == 1;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.GRINDSTONE) || event.getInventorySlots().isEmpty())
            return;
        event.setCancelled(true);
    }

    public Pair<CustomItem, GrindstoneData> checkRecipe(ItemStack item, ItemStack itemOther, int slot, Player player, InventoryView invView) {
        preCraftedRecipes.remove(player.getUniqueId());
        final var finalInputTop = new AtomicReference<CustomItem>();
        final var finalInputBottom = new AtomicReference<CustomItem>();
        final var recipeComplete = new AtomicBoolean(false);

        return customCrafting.getRegistries().getRecipes().getAvailable(RecipeType.GRINDSTONE, player).stream().filter(customRecipeGrindstone -> {
            if (!customRecipeGrindstone.checkConditions(Conditions.Data.of(player, invView))) return false;
            Ingredient ingredient = slot == 0 ? customRecipeGrindstone.getInputTop() : customRecipeGrindstone.getInputBottom();
            Optional<CustomItem> checkInput = ingredient.check(item, customRecipeGrindstone.isCheckNBT());
            if (checkInput.isEmpty()) return false; //Item is invalid! Go to next recipe!
            //Check the other ingredient
            Ingredient ingredientOther = slot == 0 ? customRecipeGrindstone.getInputBottom() : customRecipeGrindstone.getInputTop();
            if (!ItemUtils.isAirOrNull(itemOther)) {
                //Another item exists in the other slot! Check if current and other item are a valid recipe
                Optional<CustomItem> optionalOther = ingredientOther.check(itemOther, customRecipeGrindstone.isCheckNBT());
                if (optionalOther.isEmpty()) return false; //Other existing Item is invalid!
                if (slot == 0) {
                    finalInputBottom.set(optionalOther.get());
                } else {
                    finalInputTop.set(optionalOther.get());
                }
            } else if (!ingredientOther.isEmpty())
                return true; //Other slot is empty! check if current item is in a recipe, that has other ingredients! This recipe is not yet valid!
            if (slot == 0) {
                finalInputTop.set(checkInput.get());
            } else {
                finalInputBottom.set(checkInput.get());
            }
            recipeComplete.set(true);
            return true;
        }).findFirst().map(recipe -> {
            if (recipeComplete.get()) {
                return new Pair<>(recipe.getResult().getItem(player).orElse(new CustomItem(Material.AIR)), new GrindstoneData(recipe, true,
                        new IngredientData(0, recipe.getInputTop(), finalInputTop.get(), slot == 0 ? item : itemOther),
                        new IngredientData(1, recipe.getInputBottom(), finalInputBottom.get(), slot == 0 ? itemOther : item))
                );
            }
            return new Pair<>(new CustomItem(Material.AIR), (GrindstoneData) null);
        }).orElse(null);
    }

}
