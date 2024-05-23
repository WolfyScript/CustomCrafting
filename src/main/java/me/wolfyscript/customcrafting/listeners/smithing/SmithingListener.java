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

package me.wolfyscript.customcrafting.listeners.smithing;

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeSmithing;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.data.SmithingData;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.MergeOption;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.SmithingRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class SmithingListener implements Listener {

    private final HashMap<UUID, SmithingData> preCraftedRecipes = new HashMap<>();
    private final CustomCrafting customCrafting;

    private static final boolean IS_1_20 = ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0));

    public SmithingListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepare(PrepareSmithingEvent event) {
        SmithingInventory inv = event.getInventory();
        var player = (Player) event.getView().getPlayer();

        if (!ItemUtils.isAirOrNull(event.getResult())) {
            if (Stream.of(inv.getStorageContents()).map(CustomItem::getByItemStack).anyMatch(i -> i != null && i.isBlockVanillaRecipes())
                    || Bukkit.getRecipesFor(event.getResult()).stream()
                    .anyMatch(recipe -> recipe instanceof SmithingRecipe smithingRecipe && customCrafting.getDisableRecipesHandler().getRecipes().contains(NamespacedKey.fromBukkit(smithingRecipe.getKey())))) {
                event.setResult(null);
            }
        }

        var template = IS_1_20 ? inv.getItem(0) : null;
        var base = inv.getItem(CustomRecipeSmithing.BASE_SLOT);
        var addition = inv.getItem(CustomRecipeSmithing.ADDITION_SLOT);
        preCraftedRecipes.put(player.getUniqueId(), null);

        customCrafting.getRegistries().getRecipes().get(RecipeType.SMITHING).stream()
                .sorted()
                .filter(recipe -> !recipe.isDisabled())
                .map(recipe -> recipe.check(player, event.getView(), template, base, addition))
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresentOrElse(data -> {
                    preCraftedRecipes.put(player.getUniqueId(), data);
                    CustomRecipeSmithing recipe = data.getRecipe();

                    applyResult(event, inv, player, base, recipe.getResult(), recipe.isOnlyChangeMaterial(), recipe.getInternalMergeAdapters(), data);
                }, () -> {
                    if (ItemUtils.isAirOrNull(event.getResult())) return;
                    SmithingRecipe recipe = (SmithingRecipe) inv.getRecipe();
                    if (recipe == null || ICustomVanillaRecipe.isPlaceholderOrDisplayRecipe(recipe.getKey())) {
                        event.setResult(null);
                        customCrafting.getApi().getNmsUtil().getRecipeUtil().setCurrentRecipe(event.getView(), null);
                    }
                });
    }

    private static void applyResult(PrepareSmithingEvent event, SmithingInventory inv, Player player, ItemStack base, Result result, boolean onlyChangeMaterial, List<MergeAdapter> adapters, RecipeData<?> data) {
        //Process result
        Block block = inv.getLocation() != null ? inv.getLocation().getBlock() : null;
        StackReference chosenResult = result.item(player, block).orElse(StackReference.of(new ItemStack(Material.AIR)));
        ItemStack endResult = result.item(data, chosenResult, player, block);
        if (onlyChangeMaterial) {
            //Take the base item and just change the material.
            if (base == null) { // The base item may be null! Make sure to reset the result slot!
                event.setResult(null);
                return;
            }
            var baseCopy = base.clone();
            baseCopy.setType(endResult.getType());
            baseCopy.setAmount(endResult.getAmount());
            event.setResult(baseCopy);
        } else {
            if (!adapters.isEmpty()) {
                MergeOption option = new MergeOption(new int[]{CustomRecipeSmithing.BASE_SLOT});
                option.setAdapters(adapters);
                // This acts as if we appended extra merge adapters to the end of the result.
                // This makes it possible to implement the logic just once and not both in smithing listener and merge adapter.
                option.merge(data, player, block, chosenResult, endResult);
            }
            event.setResult(endResult);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCollectResult(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !(event.getClickedInventory() instanceof SmithingInventory smithingInventory)) {
            return;
        }
        final var player = (Player) event.getWhoClicked();
        final var action = event.getAction();
        final var inventory = event.getClickedInventory();
        if (event.getSlot() == CustomRecipeSmithing.RESULT_SLOT && !ItemUtils.isAirOrNull(event.getCurrentItem())) {
            if (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME || action == InventoryAction.SWAP_WITH_CURSOR) {
                return; // Ignore ingredient slots
            }
            if (preCraftedRecipes.get(player.getUniqueId()) == null) {
                // Vanilla Recipe, so lets stop here
                return;
            }

            final var smithingData = preCraftedRecipes.remove(player.getUniqueId());

            final var baseItem = inventory.getItem(CustomRecipeSmithing.BASE_SLOT) == null ? new ItemStack(Material.AIR) : inventory.getItem(CustomRecipeSmithing.BASE_SLOT).clone();
            final var additionItem = inventory.getItem(CustomRecipeSmithing.ADDITION_SLOT) == null ? new ItemStack(Material.AIR) : inventory.getItem(CustomRecipeSmithing.ADDITION_SLOT).clone();
            final var templateItem = inventory.getItem(0) == null ? new ItemStack(Material.AIR) : inventory.getItem(0).clone();

            ItemStack result = smithingInventory.getResult();
            if (result == null) {
                return;
            }

            event.setCancelled(true); // Cancel the event to prevent vanilla ingredient consumption
            // We only want to allow one craft. This is to prevent inconsistent behaviour.
            if (!handleInvalidRecipeClick(result, event)) {
                return;
            }

            player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, 1.0F, 1.0F);
            smithingData.getResult().executeExtensions(inventory.getLocation() != null ? inventory.getLocation() : player.getLocation(), inventory.getLocation() != null, player);

            smithingData.template().ifPresent(reference -> inventory.setItem(0, reference.shrink(templateItem, 1, true, inventory, player, null)));
            inventory.setItem(CustomRecipeSmithing.BASE_SLOT, smithingData.base().map(reference -> reference.shrink(baseItem, 1, true, inventory, player, null)).orElse(baseItem));
            inventory.setItem(CustomRecipeSmithing.ADDITION_SLOT, smithingData.addition().map(reference -> reference.shrink(additionItem, 1, true, inventory, player, null)).orElse(additionItem));
        }
    }

    private boolean handleInvalidRecipeClick(ItemStack result, InventoryClickEvent event) {
        // Thanks to Spigot not allowing empty ingredients, we need our own way of collecting the result!
        // Otherwise, it would just cancel the click, because there is no valid recipe!
        if (event.getClick().isShiftClick()) {
            // No space in inventory! To not mess with dropping items, etc. we just cancel the click
            return event.getView().getBottomInventory().addItem(result).isEmpty();
        }
        // A quick implementation to collect the result. Things like moving the item to the hotbar won't work!
        if (ItemUtils.isAirOrNull(event.getCursor())) {
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                event.getView().setCursor(result);
            });
        } else if (event.getCursor().isSimilar(result)) {
            if (event.getCursor().getAmount() + result.getAmount() > event.getCursor().getMaxStackSize()) {
                return false; // Again instead of doing weird stuff, just cancel
            }
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                event.getView().getCursor().setAmount(event.getCursor().getAmount() + result.getAmount());
            });
        }
        return true;
    }

}
