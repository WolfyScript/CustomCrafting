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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.inventory.ItemStack;

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

}
