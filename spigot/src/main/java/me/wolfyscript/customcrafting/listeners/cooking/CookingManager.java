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

import java.util.*;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.data.CookingRecipeData;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

import java.util.concurrent.ConcurrentHashMap;

public class CookingManager {

    private final CustomCrafting plugin;

    /**
     * Stores the custom recipes that were detected to be valid when the furnace contained a vanilla/bukkit recipe.
     * Most optimally that vanilla/bukkit recipe is the placeholder recipe of the custom recipe, but it may get overridden and has lower priority than other recipes.
     * This way if we come across a vanilla/bukkit recipe, we check the associated CustomRecipes first. When none matche then we iterate over all registered CustomRecipes.
     */
    private final Multimap<NamespacedKey, me.wolfyscript.utilities.util.NamespacedKey> VANILLA_RECIPE_TO_CUSTOM_RECIPE_CACHE = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);

    private final Map<BlockPositionData, CookingRecipeCache> cachedRecipeData = new ConcurrentHashMap<>();

    public CookingManager(CustomCrafting plugin) {
        this.plugin = plugin;
    }

    void cacheCustomBukkitRecipeAssociation(NamespacedKey bukkitRecipe, me.wolfyscript.utilities.util.NamespacedKey customRecipe) {
        VANILLA_RECIPE_TO_CUSTOM_RECIPE_CACHE.put(bukkitRecipe, customRecipe);
    }

    Collection<me.wolfyscript.utilities.util.NamespacedKey> getAssociatedCustomRecipes(NamespacedKey bukkitRecipe) {
        return VANILLA_RECIPE_TO_CUSTOM_RECIPE_CACHE.get(bukkitRecipe);
    }

    void cacheRecipeData(Block block, CookingRecipeCache recipeCache) {
        cachedRecipeData.put(new BlockPositionData(block), recipeCache);
    }

    public void clearCache(Block block) {
        cachedRecipeData.remove(new BlockPositionData(block));
    }

    /**
     * Checks if the cooked/smelted recipe is a custom recipe.<br>
     * The first invocation of this method will run the check.<br>
     * After that first call, invocations will use the cached value instead.<br>
     *
     * @param event The {@link FurnaceSmeltEvent}
     * @return If the recipe of the event is a custom recipe.
     */
    public boolean hasCustomRecipe(FurnaceSmeltEvent event) {
        return getCustomRecipeCache(event).map(recipeCache -> recipeCache.data() != null).orElse(false);
    }

    /**
     * Checks if the cooked/smelted recipe is a custom recipe.<br>
     * The first invocation of this method will run the check.<br>
     * After that first call, invocations will use the cached value instead.<br>
     *
     * @param event The {@link FurnaceSmeltEvent}
     * @return The {@link CookingRecipeData} of the custom recipe. Null if the event doesn't contain a custom recipe.
     */
    public Optional<CookingRecipeCache> getCustomRecipeCache(FurnaceSmeltEvent event) {
        return Optional.ofNullable(cachedRecipeData.get(new BlockPositionData(event.getBlock())));
    }

    public Optional<CookingRecipeCache> getCustomRecipeCache(Block block) {
        return Optional.ofNullable(cachedRecipeData.get(new BlockPositionData(block)));
    }

    private record BlockPositionData(int x, int y, int z, UUID world) {

        BlockPositionData(Block block) {
            this(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlockPositionData that = (BlockPositionData) o;
            return x == that.x && y == that.y && z == that.z && Objects.equals(world, that.world);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z, world);
        }
    }

}
