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
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
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
 * This overrides the {@link BukkitSmeltAPIAdapter} and {@link PaperSmeltAPIAdapter}, as those are just called if no data is cached yet.
 * But this listener caches data before the other adapters can be called. Therefor it is a lot more efficient in 1.17+.
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

                    CustomItem customItem = CustomItem.getByItemStack(event.getSource());
                    if (customItem != null && customItem.isBlockVanillaRecipes()) {
                        event.setTotalCookTime(Integer.MAX_VALUE);
                        return;
                    }
                    if (customCrafting.getConfigHandler().getConfig().getFurnacesSettings().isMatchVanillaRecipes()) {
                        // Try to find other vanilla/custom recipe that matches the ingredient used. This may conflict with other plugins.
                        // For that case there is the config option to simply bypass this match & block placeholder/display recipes.
                        if (!ICustomVanillaRecipe.isPlaceholderOrDisplayRecipe(event.getRecipe().getKey())) return; // if for some reason it isn't a custom recipe, lets just move on
                        Iterator<Recipe> recipeIterator = customCrafting.getApi().getNmsUtil().getRecipeUtil().recipeIterator(switch (event.getBlock().getType()) {
                            case BLAST_FURNACE -> me.wolfyscript.utilities.api.nms.inventory.RecipeType.BLASTING;
                            case SMOKER -> me.wolfyscript.utilities.api.nms.inventory.RecipeType.SMOKING;
                            default -> me.wolfyscript.utilities.api.nms.inventory.RecipeType.SMELTING;
                        });
                        while (recipeIterator.hasNext()) {
                            if (recipeIterator.next() instanceof CookingRecipe<?> recipe && !ICustomVanillaRecipe.isPlaceholderOrDisplayRecipe(recipe.getKey())) {
                                if (recipe.getInputChoice().test(event.getSource())) {
                                    NMSInventoryUtils.setCurrentRecipe(((Furnace) event.getBlock().getState()).getInventory(), new NamespacedKey(recipe.getKey().getNamespace(), recipe.getKey().getKey()));
                                    return;
                                }
                            }
                        }
                        event.setTotalCookTime(Integer.MAX_VALUE);
                    } else if (ICustomVanillaRecipe.isPlaceholderOrDisplayRecipe(event.getRecipe().getKey())){
                        event.setTotalCookTime(Integer.MAX_VALUE);
                    }
                });
    }

}
