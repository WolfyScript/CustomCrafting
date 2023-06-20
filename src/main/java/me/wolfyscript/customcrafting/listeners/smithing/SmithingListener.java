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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeSmithing;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.data.SmithingData;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.MergeOption;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
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
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.SmithingRecipe;

public class SmithingListener implements Listener {

    private final HashMap<UUID, SmithingData> preCraftedRecipes = new HashMap<>();
    private final CustomCrafting customCrafting;

    private static final boolean IS_1_20 = ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0));
    private static final int RESULT_SLOT = IS_1_20 ? 3 : 2;
    private static final int BASE_SLOT = IS_1_20 ? 1 : 0;
    private static final int ADDITION_SLOT = IS_1_20 ? 2 : 1;

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
        var base = inv.getItem(BASE_SLOT);
        var addition = inv.getItem(ADDITION_SLOT);
        preCraftedRecipes.put(player.getUniqueId(), null);

        customCrafting.getRegistries().getRecipes().getAvailable(RecipeType.SMITHING).stream()
                .map(recipe -> recipe.check(player, event.getView(), template, base, addition))
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresentOrElse(data -> {
                    preCraftedRecipes.put(player.getUniqueId(), data);
                    CustomRecipeSmithing recipe = data.getRecipe();
                    customCrafting.getApi().getNmsUtil().getRecipeUtil().setCurrentRecipe(event.getView(), ICustomVanillaRecipe.toPlaceholder(recipe.getNamespacedKey()));

                    applyResult(event, inv, player, base, recipe.getResult(), recipe.isOnlyChangeMaterial(), recipe.getInternalMergeAdapters(), data);
                }, () -> {
                    if (ItemUtils.isAirOrNull(event.getResult())) return;
                    Bukkit.getRecipesFor(event.getResult()).stream()
                            .filter(recipe -> recipe instanceof SmithingRecipe smithingRecipe && !ICustomVanillaRecipe.isPlaceholderOrDisplayRecipe(smithingRecipe.getKey()))
                            .findFirst().ifPresentOrElse(recipe -> {
                                // Valid vanilla recipe exists!
                                if (recipe instanceof Keyed keyed) {
                                    customCrafting.getApi().getNmsUtil().getRecipeUtil().setCurrentRecipe(event.getView(), NamespacedKey.fromBukkit(keyed.getKey()));
                                }
                            }, () -> {
                                event.setResult(null);
                            });
                });
    }

    private static void applyResult(PrepareSmithingEvent event, SmithingInventory inv, Player player, ItemStack base, Result result, boolean onlyChangeMaterial, List<MergeAdapter> adapters, RecipeData<?> data) {
        //Process result
        Block block = inv.getLocation() != null ? inv.getLocation().getBlock() : null;
        CustomItem chosenResult = result.getItem(player, block).orElse(new CustomItem(Material.AIR));
        ItemStack endResult = result.getItem(data, chosenResult, player, block);
        if (onlyChangeMaterial) {
            //Take the base item and just change the material.
            var baseCopy = base.clone();
            baseCopy.setType(endResult.getType());
            baseCopy.setAmount(endResult.getAmount());
            event.setResult(baseCopy);
        } else {
            if (!adapters.isEmpty()) {
                MergeOption option = new MergeOption(new int[]{BASE_SLOT});
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
        if (event.getClickedInventory() == null) return;
        if (!(event.getClickedInventory() instanceof SmithingInventory)) return;
        var player = (Player) event.getWhoClicked();
        var action = event.getAction();
        var inventory = event.getClickedInventory();
        if (event.getSlot() == RESULT_SLOT && !ItemUtils.isAirOrNull(event.getCurrentItem()) && action.equals(InventoryAction.NOTHING)) {
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

            final var smithingData = preCraftedRecipes.get(player.getUniqueId());
            smithingData.getResult().executeExtensions(inventory.getLocation() != null ? inventory.getLocation() : player.getLocation(), inventory.getLocation() != null, player);

            final var baseItem = Objects.requireNonNull(inventory.getItem(BASE_SLOT)).clone();
            final var additionItem = Objects.requireNonNull(inventory.getItem(ADDITION_SLOT)).clone();

            if (smithingData.getTemplate() != null) {
                final var templateItem = Objects.requireNonNull(inventory.getItem(0));
                inventory.setItem(0, smithingData.getTemplate().shrink(templateItem, 1, true, inventory, null, null));
            }
            inventory.setItem(BASE_SLOT, smithingData.getBase().shrink(baseItem, 1, true, inventory, null, null));
            inventory.setItem(ADDITION_SLOT, smithingData.getAddition().shrink(additionItem, 1, true, inventory, null, null));

            if (inventory.getLocation() != null) {
                inventory.getLocation().getWorld().playSound(inventory.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1, 1);
            }
            preCraftedRecipes.remove(player.getUniqueId());

            inventory.setItem(RESULT_SLOT, smithingData.getResult().getItem(smithingData, player, inventory.getLocation() != null ? inventory.getLocation().getBlock() : player.getLocation().getBlock()));
        }
    }

}
