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

package me.wolfyscript.customcrafting.listeners.cooking;

import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import com.wolfyscript.utilities.bukkit.world.items.reference.WolfyUtilsStackIdentifier;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.data.CampfireRecipeData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Campfire;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Handles campfire recipes without any NMS (which was used in previous versions of CC).
 * It checks for a valid recipe on interaction and when the recipe is done cooking.
 * The interaction does not determine the result of the recipe, that is done when the recipe is done cooking.
 */
public class CampfireListener implements Listener {

    private CustomCrafting customCrafting;

    public CampfireListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    /**
     * Since Spigot 1.20 we have access to the CampfireStartEvent that we can use to make sure the correct recipe is used and cancel invalid recipes.
     */
    @EventHandler
    public void onStartCampfireSmelt(CampfireStartEvent event) {
        ItemStack source = event.getSource();
        customCrafting.getRegistries().getRecipes().get(RecipeType.CAMPFIRE).stream()
                .filter(recipe -> recipe.checkConditions(Conditions.Data.of(event.getBlock())))
                .filter(recipe -> recipe.getSource().checkChoices(source, recipe.isCheckNBT()).isPresent())
                .findFirst()
                .ifPresentOrElse(
                        campfireRecipe -> event.setTotalCookTime(campfireRecipe.getCookingTime()),
                        () -> {
                            // Check if the CustomItem is allowed in Vanilla recipes
                            customCrafting.getApi().getCore().getRegistries().getCustomItems().getByItemStack(source)
                                    .ifPresent(customItem -> {
                                        if (customItem.isBlockVanillaRecipes()) {
                                            event.setTotalCookTime(-1); // "Cancel" the process if it is.
                                        }
                                    });
                        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractCampfire(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null || (event.getClickedBlock().getType() != Material.CAMPFIRE && event.getClickedBlock().getType() != Material.SOUL_CAMPFIRE))
            return;
        if (ItemUtils.isAirOrNull(event.getItem())) return;
        Material itemType = event.getItem().getType();
        if (itemType == Material.FIRE_CHARGE || itemType == Material.WATER_BUCKET || itemType == Material.FLINT_AND_STEEL || itemType.toString().endsWith("_SHOVEL"))
            return;

        Campfire campfire = (Campfire) event.getClickedBlock().getState();
        Conditions.Data condtionsData = Conditions.Data.of(campfire.getBlock());
        getFirstEmptySlot(campfire).ifPresent(slot -> customCrafting.getRegistries().getRecipes().get(RecipeType.CAMPFIRE).stream()
                .filter(recipe -> recipe.checkConditions(condtionsData))
                .map(recipe1 -> new Pair<>(recipe1, recipe1.getSource().checkChoices(event.getItem(), recipe1.isCheckNBT())))
                .filter(pair -> pair.getValue().isPresent())
                .findFirst()
                .ifPresentOrElse(
                        pair -> {
                            StackReference matchingSource = pair.getValue().get(); // Ignore warning. This was checked using the stream filter

                            // Calculate the items that should be placed onto the campfire
                            ItemStack stackToPlace = event.getItem().clone();
                            stackToPlace.setAmount(matchingSource.amount());

                            // Cannot handle remains here, because the original item can still be returned e.g. when the campfire is broken.
                            ItemStack stack = matchingSource.shrink(event.getItem(), 1, false, null, event.getPlayer(), event.getClickedBlock().getLocation());
                            event.getPlayer().getEquipment().setItem(event.getHand(), stack);

                            campfire.setItem(slot, stackToPlace); // Set the item that should be cooked or dropped when campfire is broken
                            campfire.setCookTimeTotal(slot, pair.getKey().getCookingTime());
                            campfire.setCookTime(slot, 0); // We need to reset the previous cook time, otherwise it is done instantly!

                            event.setCancelled(true);
                        },
                        () -> {
                            // Check if the CustomItem is allowed in Vanilla recipes
                            customCrafting.getApi().getCore().getRegistries().getCustomItems().getByItemStack(event.getItem())
                                    .ifPresent(customItem -> {
                                        if (customItem.isBlockVanillaRecipes()) {
                                            event.setUseInteractedBlock(Event.Result.DENY);
                                        }
                                    });
                        }));
        campfire.update();
    }

    private Optional<Integer> getFirstEmptySlot(Campfire campfire) {
        for (int i = 0; i < campfire.getSize(); i++) {
            if (campfire.getItem(i) == null) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    @EventHandler
    public void onCampfireFinished(BlockCookEvent event) {
        if (!event.getBlock().getType().equals(Material.CAMPFIRE)) return;

        customCrafting.getRegistries().getRecipes().get(RecipeType.CAMPFIRE).stream()
                .filter(customRecipe -> customRecipe.checkConditions(Conditions.Data.of(event.getBlock())))
                .map(customRecipe -> customRecipe.getSource().checkChoices(event.getSource(), customRecipe.isCheckNBT()).map(customItem -> {
                    IngredientData ingredientData = new IngredientData(0, 0, customRecipe.getSource(), customItem, event.getSource());
                    return new CampfireRecipeData(customRecipe, ingredientData);
                }))
                .filter(Optional::isPresent)
                .findFirst()
                .ifPresentOrElse(
                        recipeData -> recipeData.get().getRecipe().getResult().item(event.getBlock()).ifPresent(reference -> event.setResult(reference.referencedStack())),
                        () -> {
                            // Check if the CustomItem is allowed in Vanilla recipes
                            StackIdentifier identifier = WolfyUtilCore.getInstance().getRegistries().getStackIdentifierParsers().parseIdentifier(event.getSource());
                            if (identifier instanceof WolfyUtilsStackIdentifier wolfyUtilsStackIdentifier) {
                                wolfyUtilsStackIdentifier.customItem().ifPresent(customItem -> {
                                    if (customItem.isBlockVanillaRecipes()) {
                                        event.setResult(event.getSource());
                                    }
                                });
                            }
                        }
                );
    }

}
