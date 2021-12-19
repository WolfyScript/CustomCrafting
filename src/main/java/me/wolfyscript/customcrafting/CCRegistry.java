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

package me.wolfyscript.customcrafting;

import me.wolfyscript.customcrafting.gui.item_creator.tabs.ItemCreatorTab;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.customcrafting.registry.RegistryItemCreatorTabs;
import me.wolfyscript.customcrafting.registry.RegistryRecipes;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

@Deprecated
public interface CCRegistry<T extends me.wolfyscript.utilities.util.Keyed> extends Registry<T> {

    /**
     * This Registry contains all the recipes of this plugin.
     */
    RecipeRegistry RECIPES = new RecipeRegistry();
    ItemCreatorTabRegistry ITEM_CREATOR_TABS = new ItemCreatorTabRegistry();

    /**
     * The custom Registry for the Recipes of CustomCrafting.
     * Providing a lot of functionality to get the recipes you need.
     */
    @Deprecated
    final class RecipeRegistry extends WrapperRegistry<CustomRecipe<?>> {

        @Deprecated
        private RecipeRegistry() {
            super(() -> CustomCrafting.inst().getRegistries().getRecipes());
        }

        @Override
        protected RegistryRecipes getWrappedRegistry() {
            return (RegistryRecipes) super.getWrappedRegistry();
        }

        public boolean has(NamespacedKey namespacedKey) {
            return getWrappedRegistry().has(namespacedKey);
        }

        public void remove(NamespacedKey namespacedKey) {
            getWrappedRegistry().remove(namespacedKey);
        }

        @Override
        public void register(NamespacedKey namespacedKey, CustomRecipe<?> value) {
            getWrappedRegistry().register(namespacedKey, value);
        }

        @Override
        public void register(CustomRecipe<?> value) {
            getWrappedRegistry().register(value);
        }

        /**
         * @return A list of all available namespaces.
         */
        public List<String> namespaces() {
            return getWrappedRegistry().namespaces();
        }

        /**
         * @return A list of all available groups.
         */
        public List<String> groups() {
            return getWrappedRegistry().groups();
        }

        /**
         * Get all the Recipes from this group
         *
         * @param group The group to get recipes from.
         * @return The recipes contained in the group.
         */
        public List<CustomRecipe<?>> getGroup(String group) {
            return getWrappedRegistry().getGroup(group);
        }

        /**
         * Returns a List of all recipes contained in the namespace.
         *
         * @param namespace The namespace to get recipes from.
         * @return The recipes contained in the namespace.
         */
        public List<CustomRecipe<?>> get(String namespace) {
            return getWrappedRegistry().get(namespace);
        }

        public List<CustomRecipe<?>> get(CustomItem result) {
            return getWrappedRegistry().get(result);
        }

        @SuppressWarnings("unchecked")
        public <T extends CustomRecipe<?>> List<T> get(Class<T> type) {
            return getWrappedRegistry().get(type);
        }

        /**
         * @param type The type of the recipe.
         * @param <T>  The type passed via the {@link RecipeType}
         * @return A list including the {@link CustomRecipe}s of the specified {@link RecipeType}
         */
        @SuppressWarnings("unchecked")
        public <T extends CustomRecipe<?>> List<T> get(RecipeType<T> type) {
            return getWrappedRegistry().get(type);
        }

        /**
         * @param type The type of the recipe.
         * @param <T>  The type passed via the {@link RecipeType}
         * @return A list including the {@link CustomRecipe}s of the specified {@link RecipeType}
         */
        @SuppressWarnings("unchecked")
        public <T extends CustomRecipe<?>> List<T> get(RecipeType.Container<T> type) {
            return getWrappedRegistry().get(type);
        }

        public CraftingRecipe<?, AdvancedRecipeSettings> getAdvancedCrafting(NamespacedKey recipeKey) {
            return getWrappedRegistry().getAdvancedCrafting(recipeKey);
        }

        /**
         * Get all the recipes that are available.
         * Recipes that are hidden or disabled are not included.
         *
         * @return The recipes that are available and are not hidden or disabled.
         */
        public List<CustomRecipe<?>> getAvailable() {
            return getWrappedRegistry().getAvailable();
        }

        /**
         * Similar to {@link #getAvailable()} only includes the visible and enabled recipes, but also takes the player into account.
         * Recipes that the player has no permission to view are not included.
         *
         * @param player The player to get the recipes for.
         * @return The recipes that are available and the player has permission to view.
         */
        public List<CustomRecipe<?>> getAvailable(Player player) {
            return getWrappedRegistry().getAvailable(player);
        }

        /**
         * Same as {@link #get(RecipeType)}, but only includes the visible and enabled recipes.
         *
         * @param type The type of the recipe.
         * @param <T>  The type passed via the {@link RecipeType}
         * @return A list only including the {@link CustomRecipe}s of the specified {@link RecipeType}, which are enabled and visible.
         */
        public <T extends CustomRecipe<?>> List<T> getAvailable(RecipeType<T> type) {
            return getWrappedRegistry().getAvailable(type);
        }

        /**
         * Same as {@link #getAvailable(RecipeType)}, but additionally only includes recipes the player has permission to view.
         *
         * @param type   The type of the recipe.
         * @param <T>    The type passed via the {@link RecipeType}
         * @param player The player to get the recipes for.
         * @return A list only including the {@link CustomRecipe}s of the specified {@link RecipeType}, which are enabled, visible and the Player has permission to view.
         */
        public <T extends CustomRecipe<?>> List<T> getAvailable(RecipeType<T> type, Player player) {
            return getWrappedRegistry().getAvailable(type, player);
        }

        /**
         * The same as {@link #getAvailable(Player)}, but only includes the recipes that contain the similar ItemStack in the result List.
         *
         * @param result The result ItemsStack to look for.
         * @param player The player to get the recipes for.
         * @return All the recipes, that have the specified Result, are not hidden, and the player has permission to view.
         */
        public List<CustomRecipe<?>> getAvailable(ItemStack result, Player player) {
            return getWrappedRegistry().getAvailable(result, player);
        }

        public synchronized <T extends CustomRecipe<?>> List<T> getAvailable(List<T> recipes, @Nullable Player player) {
            return getWrappedRegistry().getAvailable(recipes, player);
        }

        public Stream<CraftingRecipe<?, ?>> getSimilarCraftingRecipes(CraftManager.MatrixData matrixData, boolean elite, boolean advanced) {
            return getWrappedRegistry().getSimilarCraftingRecipes(matrixData, elite, advanced);
        }

        public int size() {
            return getWrappedRegistry().size();
        }

    }

    @Deprecated
    final class ItemCreatorTabRegistry extends WrapperRegistry<ItemCreatorTab> {

        public ItemCreatorTabRegistry() {
            super(() -> CustomCrafting.inst().getRegistries().getItemCreatorTabs());
        }

    }
}
