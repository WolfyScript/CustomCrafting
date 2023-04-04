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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeSmithing;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.SmithingData;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.MergeOption;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.meta.Damageable;

public class SmithingListener implements Listener {

    private final HashMap<UUID, SmithingData> preCraftedRecipes = new HashMap<>();
    private final CustomCrafting customCrafting;

    public SmithingListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepare(PrepareSmithingEvent event) {
        SmithingInventory inv = event.getInventory();
        var player = (Player) event.getView().getPlayer();
        var base = inv.getItem(0);
        var addition = inv.getItem(1);
        if (!ItemUtils.isAirOrNull(event.getResult())) {
            if (Stream.of(inv.getStorageContents()).map(CustomItem::getByItemStack).anyMatch(i -> i != null && i.isBlockVanillaRecipes()) || Bukkit.getRecipesFor(event.getResult()).stream().anyMatch(recipe -> recipe instanceof SmithingRecipe smithingRecipe && customCrafting.getDisableRecipesHandler().getRecipes().contains(NamespacedKey.fromBukkit(smithingRecipe.getKey())))) {
                event.setResult(null);
            }
        }
        preCraftedRecipes.put(player.getUniqueId(), null);
        for (CustomRecipeSmithing recipe : customCrafting.getRegistries().getRecipes().getAvailable(RecipeType.SMITHING, player)) {
            if (recipe.checkConditions(Conditions.Data.of(player, event.getView()))) {
                Optional<CustomItem> optionalBase = recipe.getBase().check(base, recipe.isCheckNBT());
                if (optionalBase.isPresent()) {
                    Optional<CustomItem> optionalAddition = recipe.getAddition().check(addition, recipe.isCheckNBT());
                    if (optionalAddition.isPresent()) {
                        //Recipe is valid
                        assert base != null;
                        assert addition != null;
                        Result result = recipe.getResult();
                        SmithingData data = new SmithingData(recipe, new IngredientData[]{
                                new IngredientData(0, 0, recipe.getBase(), optionalBase.get(), inv.getItem(0)),
                                new IngredientData(1, 1, recipe.getAddition(), optionalAddition.get(), inv.getItem(1))}
                        );
                        preCraftedRecipes.put(player.getUniqueId(), data);
                        //Process result
                        Block block = inv.getLocation() != null ? inv.getLocation().getBlock() : null;
                        CustomItem chosenResult = result.getItem(player, block).orElse(new CustomItem(Material.AIR));
                        ItemStack endResult = result.getItem(data, chosenResult, player, block);
                        if (recipe.isOnlyChangeMaterial()) {
                            //Take the base item and just change the material.
                            var baseCopy = base.clone();
                            baseCopy.setType(endResult.getType());
                            baseCopy.setAmount(endResult.getAmount());
                            event.setResult(baseCopy);
                        } else {
                            List<MergeAdapter> adapters = recipe.getInternalMergeAdapters();
                            if (!adapters.isEmpty()) {
                                MergeOption option = new MergeOption(new int[]{0});
                                option.setAdapters(adapters);
                                // This acts as if we appended an extra merge adapter to the end of the result.
                                // This makes it possible to implement the logic just once and not both in smithing listener and merge adapter.
                                option.merge(data, player, block, chosenResult, endResult);
                            }
                            event.setResult(endResult);
                        }
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTakeOutItem(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().getType().equals(InventoryType.SMITHING)) return;
        var player = (Player) event.getWhoClicked();
        var action = event.getAction();
        var inventory = event.getClickedInventory();
        if (event.getSlot() == 2 && !ItemUtils.isAirOrNull(event.getCurrentItem()) && action.equals(InventoryAction.NOTHING)) {
            //Take out item!
            if (preCraftedRecipes.get(player.getUniqueId()) == null) {
                //Vanilla Recipe
                return;
            }
            var resultStack = event.getCurrentItem().clone();
            if (event.isShiftClick()) {
                if (InventoryUtils.hasInventorySpace(player, resultStack)) {
                    player.getInventory().addItem(resultStack);
                }
            } else if (!ItemUtils.isAirOrNull(event.getCursor())) {
                event.setCancelled(true);
                return;
            } else {
                event.getView().setCursor(resultStack);
            }
            final var baseItem = Objects.requireNonNull(inventory.getItem(0)).clone();
            final var additionItem = Objects.requireNonNull(inventory.getItem(1)).clone();
            var smithingData = preCraftedRecipes.get(player.getUniqueId());
            smithingData.getResult().executeExtensions(inventory.getLocation() != null ? inventory.getLocation() : player.getLocation(), inventory.getLocation() != null, player);
            smithingData.getBase().remove(baseItem, 1, inventory);
            smithingData.getAddition().remove(additionItem, 1, inventory);
            if (inventory.getLocation() != null) {
                inventory.getLocation().getWorld().playSound(inventory.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1, 1);
            }
            inventory.setItem(0, baseItem);
            inventory.setItem(1, additionItem);
            preCraftedRecipes.remove(player.getUniqueId());
            var smithingEvent = new PrepareSmithingEvent(event.getView(), null);
            Bukkit.getPluginManager().callEvent(smithingEvent);
            inventory.setItem(2, smithingEvent.getResult());
        }
    }

}
