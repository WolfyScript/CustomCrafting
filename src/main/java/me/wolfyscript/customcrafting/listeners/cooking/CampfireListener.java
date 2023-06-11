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
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCampfire;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.data.CampfireRecipeData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CampfireListener implements Listener {

    private static final org.bukkit.NamespacedKey ACTIVE_RECIPES_PERSISTENT = new org.bukkit.NamespacedKey(NamespacedKeyUtils.NAMESPACE, "active_recipes");
    private CustomCrafting customCrafting;

    public CampfireListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onStartCampfireSmelt(CampfireStartEvent event) {
        org.bukkit.block.Campfire blockState = (org.bukkit.block.Campfire) event.getBlock().getState();

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
                .ifPresentOrElse(campfireRecipeData -> {
                    PersistentDataContainer container = blockState.getPersistentDataContainer().getOrDefault(ACTIVE_RECIPES_PERSISTENT,
                            PersistentDataType.TAG_CONTAINER,
                            blockState.getPersistentDataContainer().getAdapterContext().newPersistentDataContainer()
                    );
                    container.set(new org.bukkit.NamespacedKey(customCrafting, String.valueOf(getCampfireInteractIndexFor(blockState))),
                            PersistentDataType.STRING,
                            campfireRecipeData.getRecipe().getNamespacedKey().toString()
                    );
                    blockState.getPersistentDataContainer().set(ACTIVE_RECIPES_PERSISTENT, PersistentDataType.TAG_CONTAINER, container);
                    blockState.update();
                    event.setTotalCookTime(campfireRecipeData.getRecipe().getCookingTime());
                }, () -> {
                    Iterator<Recipe> recipeIterator = customCrafting.getApi().getNmsUtil().getRecipeUtil().recipeIterator(me.wolfyscript.utilities.api.nms.inventory.RecipeType.CAMPFIRE_COOKING);
                    while (recipeIterator.hasNext()) {
                        if (recipeIterator.next() instanceof CookingRecipe<?> recipe && !recipe.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
                            if (recipe.getInputChoice().test(event.getSource())) {
                                // Found a vanilla or other plugin recipe that matches.

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

    private static int getCampfireInteractIndexFor(org.bukkit.block.Campfire campfireData) {
        for (int i = 0; i < campfireData.getSize(); i++) {
            if (campfireData.getItem(i) == null) return i;
        }
        return 0;
    }

    private static int getCampfireIndexFor(org.bukkit.block.Campfire campfireData, ItemStack source) {
        for (int i = 0; i < campfireData.getSize(); i++) {
            if (Objects.equals(campfireData.getItem(i), source)) return i;
        }
        return 0;
    }

    @EventHandler
    public void onCampfireFinished(BlockCookEvent event) {
        if (!event.getBlock().getType().equals(Material.CAMPFIRE)) return;
        org.bukkit.block.Campfire campfireData = (org.bukkit.block.Campfire) event.getBlock().getState();

        PersistentDataContainer container = campfireData.getPersistentDataContainer().getOrDefault(ACTIVE_RECIPES_PERSISTENT,
                PersistentDataType.TAG_CONTAINER,
                campfireData.getPersistentDataContainer().getAdapterContext().newPersistentDataContainer()
        );
        String recipeKey = container.get(new org.bukkit.NamespacedKey(customCrafting, String.valueOf(getCampfireIndexFor(campfireData, event.getSource()))), PersistentDataType.STRING);
        if (recipeKey != null) {
            NamespacedKey namespacedKey = NamespacedKey.of(recipeKey);
            CustomRecipe<?> recipeCampfire = customCrafting.getRegistries().getRecipes().get(namespacedKey);
            if (recipeCampfire instanceof CustomRecipeCampfire customRecipeCampfire) {
                customRecipeCampfire.getResult().getItem(event.getBlock()).ifPresent(customItem -> {
                    event.setResult(customItem.create());
                });
            }
        } else {
            // Check if the CustomItem is allowed in Vanilla recipes
            CustomItem customItem = CustomItem.getByItemStack(event.getSource());
            if (customItem != null && customItem.isBlockVanillaRecipes()) {
                event.setResult(event.getSource());
            }

        }
    }

}
