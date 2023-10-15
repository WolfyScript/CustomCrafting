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
import com.wolfyscript.utilities.bukkit.world.items.reference.WolfyUtilsStackIdentifier;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.data.CampfireRecipeData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Campfire;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;
import java.util.Objects;
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
        getFirstEmptySlot(campfire).ifPresent(slot -> customCrafting.getRegistries().getRecipes().get(RecipeType.CAMPFIRE).stream()
                .filter(recipe1 -> recipe1.getSource().checkChoices(event.getItem(), recipe1.isCheckNBT()).isPresent() && recipe1.checkConditions(Conditions.Data.of(campfire.getBlock())))
                .findFirst()
                .ifPresentOrElse(
                        recipe1 -> campfire.setCookTimeTotal(slot, recipe1.getCookingTime()),
                        () -> {
                            boolean isPartOfPlaceholderRecipe = false;
                            Iterator<Recipe> recipeIterator = customCrafting.getApi().getNmsUtil().getRecipeUtil().recipeIterator(me.wolfyscript.utilities.api.nms.inventory.RecipeType.CAMPFIRE_COOKING);
                            while (recipeIterator.hasNext()) {
                                if (recipeIterator.next() instanceof CookingRecipe<?> recipe) {
                                    if (!recipe.getInputChoice().test(event.getItem())) continue;
                                    if (ICustomVanillaRecipe.isPlaceholderOrDisplayRecipe(recipe.getKey())) {
                                        isPartOfPlaceholderRecipe = true;
                                        continue;
                                    }
                                    // Found a vanilla or other plugin recipe that matches.
                                    campfire.setCookTimeTotal(slot, recipe.getCookingTime());

                                    // Check if the CustomItem is allowed in Vanilla recipes
                                    customCrafting.getApi().getCore().getRegistries().getCustomItems().getByItemStack(event.getItem())
                                            .ifPresent(customItem -> {
                                                if (customItem.isBlockVanillaRecipes()) {
                                                    event.setUseInteractedBlock(Event.Result.DENY);
                                                }
                                            });
                                    return;
                                }
                            }
                            // No non-cc recipe found!
                            // Allow for normal interaction if the item is not used in any recipe.
                            if (isPartOfPlaceholderRecipe) {
                                event.setUseInteractedBlock(Event.Result.DENY);
                            }
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
                .map(recipe1 -> recipe1.getSource().checkChoices(event.getSource(), recipe1.isCheckNBT()).map(customItem -> {
                    if (recipe1.checkConditions(Conditions.Data.of(event.getBlock()))) {
                        IngredientData ingredientData = new IngredientData(0, 0, recipe1.getSource(), customItem, event.getSource());
                        return new CampfireRecipeData(recipe1, ingredientData);
                    }
                    return null;
                }).orElse(null))
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresentOrElse(campfireRecipeData -> campfireRecipeData.getRecipe().getResult().item(event.getBlock()).ifPresent(reference -> event.setResult(reference.stack())), () -> {
                    Iterator<Recipe> recipeIterator = customCrafting.getApi().getNmsUtil().getRecipeUtil().recipeIterator(me.wolfyscript.utilities.api.nms.inventory.RecipeType.CAMPFIRE_COOKING);
                    while (recipeIterator.hasNext()) {
                        if (recipeIterator.next() instanceof CookingRecipe<?> recipe && !ICustomVanillaRecipe.isPlaceholderOrDisplayRecipe(recipe.getKey())) {
                            if (recipe.getInputChoice().test(event.getSource())) {
                                // Found a vanilla or other plugin recipe that matches.
                                event.setResult(recipe.getResult());

                                // Check if the CustomItem is allowed in Vanilla recipes
                                StackIdentifier identifier = WolfyUtilCore.getInstance().getRegistries().getStackIdentifierParsers().parseIdentifier(event.getSource());
                                if (identifier instanceof WolfyUtilsStackIdentifier wolfyUtilsStackIdentifier) {
                                    wolfyUtilsStackIdentifier.customItem().ifPresent(customItem -> {
                                        if (customItem.isBlockVanillaRecipes()) {
                                            event.setResult(event.getSource());
                                        }
                                    });
                                }
                                return;
                            }
                        }
                    }
                    // No non-cc recipe found, lets cancel the progress!
                    event.setResult(event.getSource());
                });
    }

}
