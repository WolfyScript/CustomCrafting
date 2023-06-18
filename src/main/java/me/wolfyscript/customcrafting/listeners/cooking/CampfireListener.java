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

import java.util.Iterator;
import java.util.Objects;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.data.CampfireRecipeData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;

public class CampfireListener implements Listener {

    private CustomCrafting customCrafting;

    public CampfireListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onStartCampfireSmelt(CampfireStartEvent event) {
        customCrafting.getRegistries().getRecipes().get(RecipeType.CAMPFIRE).stream()
                .map(recipe1 -> recipe1.getSource().check(event.getSource(), recipe1.isCheckNBT()).map(customItem -> {
                    if (recipe1.checkConditions(Conditions.Data.of(event.getBlock()))) {
                        IngredientData ingredientData = new IngredientData(0, 0, recipe1.getSource(), customItem, event.getSource());
                        return new CampfireRecipeData(recipe1, ingredientData);
                    }
                    return null;
                }).orElse(null))
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresentOrElse(campfireRecipeData -> event.setTotalCookTime(campfireRecipeData.getRecipe().getCookingTime()), () -> {
                    Iterator<Recipe> recipeIterator = customCrafting.getApi().getNmsUtil().getRecipeUtil().recipeIterator(me.wolfyscript.utilities.api.nms.inventory.RecipeType.CAMPFIRE_COOKING);
                    while (recipeIterator.hasNext()) {
                        if (recipeIterator.next() instanceof CookingRecipe<?> recipe && !recipe.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
                            if (recipe.getInputChoice().test(event.getSource())) {
                                // Found a vanilla or other plugin recipe that matches.
                                event.setTotalCookTime(recipe.getCookingTime());

                                // Check if the CustomItem is allowed in Vanilla recipes
                                CustomItem customItem = CustomItem.getByItemStack(event.getSource());
                                if (customItem != null && customItem.isBlockVanillaRecipes()) {
                                    event.setTotalCookTime(-1); // "Cancel" the process if it is.
                                }
                                return;
                            }
                        }
                    }
                    // No non-cc recipe found, lets cancel the progress!
                    event.setTotalCookTime(-1);
                });
    }

    @EventHandler
    public void onCampfireFinished(BlockCookEvent event) {
        if (!event.getBlock().getType().equals(Material.CAMPFIRE)) return;
        customCrafting.getRegistries().getRecipes().get(RecipeType.CAMPFIRE).stream()
                .map(recipe1 -> recipe1.getSource().check(event.getSource(), recipe1.isCheckNBT()).map(customItem -> {
                    if (recipe1.checkConditions(Conditions.Data.of(event.getBlock()))) {
                        IngredientData ingredientData = new IngredientData(0, 0, recipe1.getSource(), customItem, event.getSource());
                        return new CampfireRecipeData(recipe1, ingredientData);
                    }
                    return null;
                }).orElse(null))
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresentOrElse(campfireRecipeData -> campfireRecipeData.getRecipe().getResult().getItem(event.getBlock()).ifPresent(customItem -> event.setResult(customItem.create())), () -> {
                    Iterator<Recipe> recipeIterator = customCrafting.getApi().getNmsUtil().getRecipeUtil().recipeIterator(me.wolfyscript.utilities.api.nms.inventory.RecipeType.CAMPFIRE_COOKING);
                    while (recipeIterator.hasNext()) {
                        if (recipeIterator.next() instanceof CookingRecipe<?> recipe && !recipe.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
                            if (recipe.getInputChoice().test(event.getSource())) {
                                // Found a vanilla or other plugin recipe that matches.
                                event.setResult(recipe.getResult());

                                // Check if the CustomItem is allowed in Vanilla recipes
                                CustomItem customItem = CustomItem.getByItemStack(event.getSource());
                                if (customItem != null && customItem.isBlockVanillaRecipes()) {
                                    event.setResult(event.getSource());
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
