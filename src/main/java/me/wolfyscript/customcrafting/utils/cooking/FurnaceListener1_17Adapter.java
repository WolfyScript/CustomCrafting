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

package me.wolfyscript.customcrafting.utils.cooking;

import com.wolfyscript.utilities.bukkit.nms.inventory.NMSInventoryUtils;
import java.util.Iterator;
import java.util.Objects;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;

/**
 * Uses the new {@link FurnaceStartSmeltEvent} to more efficiently handle custom cooking recipes.
 */
public class FurnaceListener1_17Adapter implements Listener {

    private final CustomCrafting customCrafting;
    private final CookingManager manager;

    public FurnaceListener1_17Adapter(CustomCrafting customCrafting, CookingManager manager) {
        this.customCrafting = customCrafting;
        this.manager = manager;
    }

    @EventHandler
    public void onStartSmelt(FurnaceStartSmeltEvent event) {
        manager.clearCache(event.getBlock());
        customCrafting.getRegistries().getRecipes().get((RecipeType<? extends CustomRecipeCooking<?, ?>>) switch (event.getBlock().getType()) {
                    case BLAST_FURNACE -> RecipeType.BLAST_FURNACE;
                    case SMOKER -> RecipeType.SMOKER;
                    default -> RecipeType.FURNACE;
                }).stream()
                .map(recipe1 -> manager.getAdapter().processRecipe(recipe1, event.getSource(), event.getBlock()).getKey())
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresentOrElse(data -> {
                    manager.cacheRecipeData(event.getBlock(), new Pair<>(data, true));
                    NMSInventoryUtils.setCurrentRecipe(((Furnace) event.getBlock().getState()).getInventory(), data.getRecipe().getNamespacedKey());
                }, () -> {
                    manager.clearCache(event.getBlock());
                    Iterator<Recipe> recipeIterator = customCrafting.getApi().getNmsUtil().getRecipeUtil().recipeIterator(switch (event.getBlock().getType()) {
                        case BLAST_FURNACE -> me.wolfyscript.utilities.api.nms.inventory.RecipeType.BLASTING;
                        case SMOKER -> me.wolfyscript.utilities.api.nms.inventory.RecipeType.SMOKING;
                        default -> me.wolfyscript.utilities.api.nms.inventory.RecipeType.SMELTING;
                    });
                    while (recipeIterator.hasNext()) {
                        if (recipeIterator.next() instanceof CookingRecipe<?> recipe && !recipe.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
                            if (recipe.getInputChoice().test(event.getSource())) {
                                NMSInventoryUtils.setCurrentRecipe(((Furnace) event.getBlock().getState()).getInventory(), new NamespacedKey(recipe.getKey().getNamespace(), recipe.getKey().getKey()));

                                //Check if the CustomItem is allowed in Vanilla recipes
                                CustomItem customItem = CustomItem.getByItemStack(event.getSource());
                                if (customItem != null && customItem.isBlockVanillaRecipes()) {
                                    event.setTotalCookTime(Integer.MAX_VALUE); //"Cancel" the process if it is.
                                    manager.cacheRecipeData(event.getBlock(), new Pair<>(null, false));
                                }
                                return;
                            }
                        }
                    }
                    NMSInventoryUtils.setCurrentRecipe(((Furnace) event.getBlock().getState()).getInventory(), null);
                    event.setTotalCookTime(Integer.MAX_VALUE);
                });
    }

}
