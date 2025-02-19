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

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.data.GrindstoneData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class GrindStoneListener implements Listener {

    private static final HashMap<UUID, GrindstoneData> preCraftedRecipes = new HashMap<>();
    private final CustomCrafting customCrafting;

    public GrindStoneListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCollectResult(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getAction().equals(InventoryAction.NOTHING) || !event.getClickedInventory().getType().equals(InventoryType.GRINDSTONE))
            return;
        var player = (Player) event.getWhoClicked();
        var action = event.getAction();
        var inventory = event.getClickedInventory();
        if (event.getSlot() == 2 && !ItemUtils.isAirOrNull(inventory.getItem(2)) && (action.toString().startsWith("PICKUP_") || action.equals(InventoryAction.COLLECT_TO_CURSOR) || action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))) {
            if (preCraftedRecipes.get(player.getUniqueId()) == null) {
                //Vanilla Recipe
                return;
            }
            event.setCancelled(true);

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

            inventory.getLocation().getWorld().playSound(inventory.getLocation(), Sound.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1f, 1f);

            var grindstoneData = preCraftedRecipes.get(player.getUniqueId());
            if (grindstoneData.getRecipe().getXp() > 0) { //Spawn xp
                ExperienceOrb orb = (ExperienceOrb) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(grindstoneData.getRecipe().getXp());
            }
            grindstoneData.getResult().executeExtensions(inventory.getLocation() != null ? inventory.getLocation() : player.getLocation(), inventory.getLocation() != null, player);

            grindstoneData.topIngredient().ifPresent(ingredientTop -> {
                if (!ItemUtils.isAirOrNull(ingredientTop.reference().referencedStack())) {
                    ItemStack itemTop = inventory.getItem(0);
                    if (!ItemUtils.isAirOrNull(itemTop)) {
                        inventory.setItem(0, ingredientTop.reference().shrink(itemTop, 1, ingredientTop.ingredient().isReplaceWithRemains(), inventory, player, null));
                    }
                }
            });
            grindstoneData.bottomIngredient().ifPresent(ingredientBottom -> {
                if (!ItemUtils.isAirOrNull(ingredientBottom.reference().referencedStack())) {
                    ItemStack itemBottom = inventory.getItem(1);
                    if (!ItemUtils.isAirOrNull(itemBottom)) {
                        inventory.setItem(1, ingredientBottom.reference().shrink(itemBottom, 1, ingredientBottom.ingredient().isReplaceWithRemains(), inventory, player, null));
                    }
                }
            });
            // Invalidate crafted recipe and check for new recipe
            preCraftedRecipes.remove(player.getUniqueId());
            processGrindstone(inventory, player, event);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !event.getView().getTopInventory().getType().equals(InventoryType.GRINDSTONE))
            return;
        var player = (Player) event.getWhoClicked();
        var inventory = event.getView().getTopInventory();
        if (event.getClickedInventory().getType() == InventoryType.GRINDSTONE && event.getSlot() != 2) {
            InventoryUtils.calculateClickedSlot(event); // Different from the vanilla behaviour this allows the player to place every item into the Grindstone
        }
        processGrindstone(inventory, player, event);
        Bukkit.getScheduler().runTask(customCrafting, player::updateInventory); // This is required for actions to be visible in the Grindstone inventory, otherwise it's very glitchy.
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.GRINDSTONE) || event.getInventorySlots().isEmpty())
            return;
        InventoryView view = event.getView();
        if (event.getRawSlots().stream().noneMatch(integer -> view.getInventory(integer) instanceof GrindstoneInventory))
            return;
        event.setCancelled(true);
    }

    private void processGrindstone(Inventory inventory, Player player, InventoryInteractEvent event) {
        lookForValidRecipe(inventory.getItem(0), inventory.getItem(1), player, event.getView())
                .ifPresentOrElse(data -> {
                    inventory.setItem(2, data.getResult().item(player).map(reference -> reference.referencedStack(ctxBldr -> ctxBldr.player(player))).orElse(new ItemStack(Material.AIR)));
                    preCraftedRecipes.put(player.getUniqueId(), data);
                }, () -> {
                    for (ItemStack itemStack : new ItemStack[]{inventory.getItem(0), inventory.getItem(1)}) {
                        if (customCrafting.getApi().getRegistries().getCustomItems().getByItemStack(itemStack).map(CustomItem::isBlockVanillaRecipes).orElse(false)) {
                            inventory.setItem(2, null);
                            return;
                        }
                    }
                });
    }

    private Optional<GrindstoneData> lookForValidRecipe(ItemStack topStack, ItemStack bottomStack, Player player, InventoryView invView) {
        preCraftedRecipes.remove(player.getUniqueId());
        Conditions.Data data = Conditions.Data.of(player, invView);
        return customCrafting.getRegistries().getRecipes().get(RecipeType.GRINDSTONE).stream()
                .sorted()
                .filter(recipe -> !recipe.isDisabled() && recipe.checkConditions(data))
                .map(recipe -> {
                    var topIngredient = recipe.getInputTop();
                    var bottomIngredient = recipe.getInputBottom();
                    // Check the top ingredient
                    Optional<StackReference> checkTop = topIngredient.checkChoices(topStack, recipe.isCheckNBT());
                    if (checkTop.isEmpty() && (!topIngredient.isEmpty() && !topIngredient.isAllowEmpty())) return null;
                    // Check the bottom ingredient
                    Optional<StackReference> checkBottom = bottomIngredient.checkChoices(bottomStack, recipe.isCheckNBT());
                    if (checkBottom.isEmpty() && (!bottomIngredient.isEmpty() && !bottomIngredient.isAllowEmpty())) return null;

                    return new GrindstoneData(recipe, true,
                            checkTop.map(stackReference -> new IngredientData(0, 0, recipe.getInputTop(), stackReference, topStack)).orElse(null),
                            checkBottom.map(stackReference -> new IngredientData(1, 1, recipe.getInputBottom(), stackReference, bottomStack)).orElse(null));
                })
                .filter(Objects::nonNull)
                .findFirst();
    }

}
