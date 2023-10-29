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
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * In Spigot 1.20 we have access to the CampfireStartEvent that we can use to make sure the correct recipe is used and cancel invalid recipes.
 */
public class Campfire1_20Listener implements Listener {

    private CustomCrafting customCrafting;

    public Campfire1_20Listener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onStartCampfireSmelt(CampfireStartEvent event) {
        ItemStack source = event.getSource();
        customCrafting.getRegistries().getRecipes().get(RecipeType.CAMPFIRE).stream()
                .filter(recipe1 -> recipe1.getSource().checkChoices(source, recipe1.isCheckNBT()).isPresent() && recipe1.checkConditions(Conditions.Data.of(event.getBlock())))
                .findFirst()
                .ifPresentOrElse(
                        campfireRecipe -> event.setTotalCookTime(campfireRecipe.getCookingTime()),
                        () -> {
                            Iterator<Recipe> recipeIterator = customCrafting.getApi().getNmsUtil().getRecipeUtil().recipeIterator(me.wolfyscript.utilities.api.nms.inventory.RecipeType.CAMPFIRE_COOKING);
                            while (recipeIterator.hasNext()) {
                                if (recipeIterator.next() instanceof CookingRecipe<?> recipe && !ICustomVanillaRecipe.isPlaceholderOrDisplayRecipe(recipe.getKey())) {
                                    if (recipe.getInputChoice().test(source)) {
                                        // Found a vanilla or other plugin recipe that matches.
                                        event.setTotalCookTime(recipe.getCookingTime());

                                        // Check if the CustomItem is allowed in Vanilla recipes
                                        customCrafting.getApi().getCore().getRegistries().getCustomItems().getByItemStack(source)
                                                .ifPresent(customItem -> {
                                                    if (customItem.isBlockVanillaRecipes()) {
                                                        event.setTotalCookTime(-1); // "Cancel" the process if it is.
                                                    }
                                                });
                                        return;
                                    }
                                }
                            }
                            // No non-cc recipe found, lets cancel the progress!
                            event.setTotalCookTime(-1);
                        });
    }

}
