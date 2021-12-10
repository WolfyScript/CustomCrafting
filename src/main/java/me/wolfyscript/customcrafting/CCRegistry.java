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
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    class RecipeRegistry extends Registry.SimpleRegistry<CustomRecipe<?>> {

        private final Map<String, List<CustomRecipe<?>>> BY_NAMESPACE = new HashMap<>();
        private final Map<String, List<CustomRecipe<?>>> BY_GROUP = new HashMap<>();
        private final Map<CustomItem, List<CustomRecipe<?>>> BY_RESULT = new HashMap<>();
        private final Map<Class<?>, List<CustomRecipe<?>>> BY_CLASS_TYPE = new HashMap<>();
        private final Map<RecipeType<?>, List<CustomRecipe<?>>> BY_RECIPE_TYPE = new HashMap<>();
        private final Map<RecipeType.Container<?>, List<CustomRecipe<?>>> BY_RECIPE_TYPE_CONTAINER = new HashMap<>();
        private final Set<String> NAMESPACES = new HashSet<>();
        private final Set<String> GROUPS = new HashSet<>();

        private RecipeRegistry() {
        }

        public boolean has(NamespacedKey namespacedKey) {
            return this.map.containsKey(namespacedKey);
        }

        public void remove(NamespacedKey namespacedKey) {
            if (get(namespacedKey) instanceof ICustomVanillaRecipe) {
                removeBukkitRecipe(namespacedKey);
            }
            this.map.remove(namespacedKey);
            clearCache(namespacedKey);
        }

        /**
         * Clears the cache, so it can update with the added/removed value.<br>
         * <br>
         * This is needed as the cache needs to be updated with the possible new values.<br>
         * Of course, you could check if the specific caches must be cleared, but that would be at the cost of register/remove performance.<br>
         * (Most servers have a fixed set of recipes that they use and don't frequently change in production... well at least they shouldn't)
         *
         * @param key The key of the recipe that caused the reset.
         */
        private void clearCache(NamespacedKey key) {
            BY_NAMESPACE.remove(key.getNamespace());
            BY_GROUP.clear();
            NAMESPACES.clear();
            GROUPS.clear();
            BY_RESULT.clear();
            BY_CLASS_TYPE.clear();
            BY_RECIPE_TYPE.clear();
            BY_RECIPE_TYPE_CONTAINER.clear();
        }

        @Override
        public void register(NamespacedKey namespacedKey, CustomRecipe<?> value) {
            remove(Objects.requireNonNull(namespacedKey, "Not a valid key! The key cannot be null!"));
            super.register(namespacedKey, value);
            if (value instanceof ICustomVanillaRecipe vanillaRecipe && !value.isDisabled()) {
                try {
                    Bukkit.addRecipe(vanillaRecipe.getVanillaRecipe());
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    CustomCrafting.inst().getLogger().warning(String.format("Failed to add recipe '%s' to Bukkit: %s", namespacedKey, ex.getMessage()));
                }
            }
            clearCache(namespacedKey);
        }

        @Override
        public void register(CustomRecipe<?> value) {
            this.register(value.getNamespacedKey(), value);
        }

        /**
         * @return A list of all available namespaces.
         */
        public List<String> namespaces() {
            if(NAMESPACES.isEmpty()) {
                NAMESPACES.addAll(keySet().stream().map(NamespacedKey::getNamespace).distinct().toList());
            }
            return new ArrayList<>(NAMESPACES);
        }

        /**
         * @return A list of all available groups.
         */
        public List<String> groups() {
            if (GROUPS.isEmpty()) {
                GROUPS.addAll(values().stream().map(CustomRecipe::getGroup).filter(group -> !group.isEmpty()).distinct().toList());
            }
            return new ArrayList<>(GROUPS);
        }

        /**
         * Get all the Recipes from this group
         *
         * @param group The group to get recipes from.
         * @return The recipes contained in the group.
         */
        public List<CustomRecipe<?>> getGroup(String group) {
            return BY_GROUP.computeIfAbsent(group, s -> values().stream().filter(r -> r.getGroup().equals(s)).collect(Collectors.toList()));
        }

        /**
         * Returns a List of all recipes contained in the namespace.
         *
         * @param namespace The namespace to get recipes from.
         * @return The recipes contained in the namespace.
         */
        public List<CustomRecipe<?>> get(String namespace) {
            return BY_NAMESPACE.computeIfAbsent(namespace, s -> entrySet().stream().filter(entry -> entry.getKey().getNamespace().equalsIgnoreCase(s)).map(Map.Entry::getValue).collect(Collectors.toList()));
        }

        public List<CustomRecipe<?>> get(CustomItem result) {
            return BY_RESULT.computeIfAbsent(result, item -> values().stream().filter(recipe -> recipe.getResult().getChoices().contains(item)).collect(Collectors.toList()));
        }

        @SuppressWarnings("unchecked")
        public <T extends CustomRecipe<?>> List<T> get(Class<T> type) {
            return (List<T>) BY_CLASS_TYPE.computeIfAbsent(type, aClass -> (List<CustomRecipe<?>>) values().stream().filter(aClass::isInstance).map(aClass::cast).collect(Collectors.toList()));
        }

        /**
         * @param type The type of the recipe.
         * @param <T>  The type passed via the {@link RecipeType}
         * @return A list including the {@link CustomRecipe}s of the specified {@link RecipeType}
         */
        @SuppressWarnings("unchecked")
        public <T extends CustomRecipe<?>> List<T> get(RecipeType<T> type) {
            return (List<T>) BY_RECIPE_TYPE.computeIfAbsent(type, recipeType -> values().stream().filter(recipeType::isInstance).map(recipeType::cast).collect(Collectors.toList()));
        }

        /**
         * @param type The type of the recipe.
         * @param <T>  The type passed via the {@link RecipeType}
         * @return A list including the {@link CustomRecipe}s of the specified {@link RecipeType}
         */
        @SuppressWarnings("unchecked")
        public <T extends CustomRecipe<?>> List<T> get(RecipeType.Container<T> type) {
            return (List<T>) BY_RECIPE_TYPE_CONTAINER.computeIfAbsent(type, container -> values().stream().filter(container::isInstance).map(container::cast).collect(Collectors.toList()));
        }

        public CraftingRecipe<?, AdvancedRecipeSettings> getAdvancedCrafting(NamespacedKey recipeKey) {
            CustomRecipe<?> customRecipe = CCRegistry.RECIPES.get(recipeKey);
            return RecipeType.Container.CRAFTING.isInstance(customRecipe) ? RecipeType.Container.CRAFTING.cast(customRecipe) : null;
        }

        /**
         * Get all the recipes that are available.
         * Recipes that are hidden or disabled are not included.
         *
         * @return The recipes that are available and are not hidden or disabled.
         */
        public List<CustomRecipe<?>> getAvailable() {
            return getAvailable(values().stream());
        }

        /**
         * Similar to {@link #getAvailable()} only includes the visible and enabled recipes, but also takes the player into account.
         * Recipes that the player has no permission to view are not included.
         *
         * @param player The player to get the recipes for.
         * @return The recipes that are available and the player has permission to view.
         */
        public List<CustomRecipe<?>> getAvailable(Player player) {
            return getAvailable(getAvailable(), player);
        }

        /**
         * Same as {@link #get(RecipeType)}, but only includes the visible and enabled recipes.
         *
         * @param type The type of the recipe.
         * @param <T>  The type passed via the {@link RecipeType}
         * @return A list only including the {@link CustomRecipe}s of the specified {@link RecipeType}, which are enabled and visible.
         */
        public <T extends CustomRecipe<?>> List<T> getAvailable(RecipeType<T> type) {
            return getAvailable(get(type.getRecipeClass()).stream());
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
            return getAvailable(getAvailable(type), player);
        }

        /**
         * The same as {@link #getAvailable(Player)}, but only includes the recipes that contain the similar ItemStack in the result List.
         *
         * @param result The result ItemsStack to look for.
         * @param player The player to get the recipes for.
         * @return All the recipes, that have the specified Result, are not hidden, and the player has permission to view.
         */
        public List<CustomRecipe<?>> getAvailable(ItemStack result, Player player) {
            return getAvailable(player).stream().filter(recipe -> recipe.findResultItem(result)).collect(Collectors.toList());
        }

        public synchronized <T extends CustomRecipe<?>> List<T> getAvailable(List<T> recipes, @Nullable Player player) {
            return getAvailable(recipes.stream().filter(recipe -> recipe.checkCondition("permission", new Conditions.Data(player))));
        }

        /**
         * Filters the Recipes stream from disabled or hidden recipes, and sorts the list according to the {@link me.wolfyscript.customcrafting.recipes.RecipePriority}!
         *
         * @param recipes The Stream of Recipes to filter.
         * @param <T>     The type of the {@link CustomRecipe}
         * @return A filtered {@link List} containing only visible and enabled recipes.
         */
        private <T extends CustomRecipe<?>> List<T> getAvailable(Stream<T> recipes) {
            return recipes.filter(recipe -> !recipe.isHidden() && !recipe.isDisabled()).sorted(Comparator.comparing(CustomRecipe::getPriority)).collect(Collectors.toList());
        }

        public Stream<CraftingRecipe<?, ?>> getSimilarCraftingRecipes(CraftManager.MatrixData matrixData, boolean elite, boolean advanced) {
            List<CraftingRecipe<?, ?>> craftingRecipes = new ArrayList<>();
            if (elite) {
                craftingRecipes.addAll(get(RecipeType.Container.ELITE_CRAFTING));
            }
            if (advanced) {
                craftingRecipes.addAll(get(RecipeType.Container.CRAFTING));
            }
            return craftingRecipes.stream().filter(recipe -> recipe.fitsDimensions(matrixData)).sorted(Comparator.comparing(CustomRecipe::getPriority));
        }

        public int size() {
            return this.map.size();
        }

        private void removeBukkitRecipe(NamespacedKey namespacedKey) {
            Bukkit.removeRecipe(namespacedKey.toBukkit(CustomCrafting.inst()));
        }
    }

    class ItemCreatorTabRegistry extends SimpleRegistry<ItemCreatorTab> {

        @Override
        public void register(NamespacedKey namespacedKey, ItemCreatorTab value) {
            super.register(namespacedKey, value);
        }
    }
}
