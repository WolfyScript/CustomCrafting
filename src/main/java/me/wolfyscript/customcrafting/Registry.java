package me.wolfyscript.customcrafting;

import me.wolfyscript.customcrafting.gui.item_creator.tabs.ItemCreatorTab;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.*;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.customcrafting.utils.recipe_item.extension.ResultExtension;
import me.wolfyscript.customcrafting.utils.recipe_item.target.MergeAdapter;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Registry<T extends me.wolfyscript.utilities.util.Keyed> extends me.wolfyscript.utilities.util.Registry<T> {

    /**
     * This Registry contains all the recipes of this plugin.
     */
    RecipeRegistry RECIPES = new RecipeRegistry();
    /**
     * This Registry contains all the custom Result Extensions that can be saved to a Result.
     */
    SimpleRegistry<ResultExtension> RESULT_EXTENSIONS = new SimpleRegistry<>();
    SimpleRegistry<MergeAdapter> RESULT_MERGE_ADAPTERS = new SimpleRegistry<>();
    ItemCreatorTabRegistry ITEM_CREATOR_TABS = new ItemCreatorTabRegistry();

    /**
     * The custom Registry for the Recipes of CustomCrafting.
     * Providing a lot of functionality to get the recipes you need.
     */
    class RecipeRegistry extends SimpleRegistry<ICustomRecipe<?, ?>> {

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
        }

        @Override
        public void register(NamespacedKey namespacedKey, ICustomRecipe<?, ?> value) {
            remove(Objects.requireNonNull(namespacedKey, "Not a valid key! The key cannot be null!"));
            if (value instanceof ICraftingRecipe craftingRecipe) {
                craftingRecipe.constructRecipe();
            }
            super.register(namespacedKey, value);
            if (value instanceof ICustomVanillaRecipe) {
                try {
                    Bukkit.addRecipe(((ICustomVanillaRecipe<?>) value).getVanillaRecipe());
                } catch (IllegalArgumentException ex) {
                    CustomCrafting.inst().getLogger().warning(String.format("Failed to add recipe '%s' to Bukkit: %s", namespacedKey, ex.getMessage()));
                }
            }
        }

        @Override
        public void register(ICustomRecipe<?, ?> value) {
            this.register(value.getNamespacedKey(), value);
        }

        /**
         * @return A list of all available namespaces.
         */
        public List<String> namespaces() {
            return keySet().parallelStream().map(NamespacedKey::getNamespace).distinct().collect(Collectors.toList());
        }

        /**
         * @return A list of all available groups.
         */
        public List<String> groups() {
            return values().parallelStream().map(ICustomRecipe::getGroup).filter(group -> !group.isEmpty()).distinct().collect(Collectors.toList());
        }

        /**
         * Returns a List of all recipes contained in the namespace.
         *
         * @param namespace The namespace to get recipes from.
         * @return The recipes contained in the namespace.
         */
        public List<ICustomRecipe<?, ?>> get(String namespace) {
            return entrySet().parallelStream().filter(entry -> entry.getKey().getNamespace().equalsIgnoreCase(namespace)).map(Map.Entry::getValue).collect(Collectors.toList());
        }

        /**
         * Get all the Recipes from this group
         *
         * @param group The group to get recipes from.
         * @return The recipes contained in the group.
         */
        public List<ICustomRecipe<?, ?>> getGroup(String group) {
            return Registry.RECIPES.values().parallelStream().filter(r -> r.getGroup().equals(group)).collect(Collectors.toList());
        }

        public List<ICustomRecipe<?, ?>> get(CustomItem result) {
            return values().parallelStream().filter(recipe -> recipe.getResult().getChoices().contains(result)).collect(Collectors.toList());
        }

        public <T extends ICustomRecipe<?, ?>> List<T> get(Class<T> type) {
            return values().parallelStream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
        }

        /**
         * @param type The type of the recipe.
         * @param <T>  The type passed via the {@link RecipeType}
         * @return A list including the {@link ICustomRecipe}s of the specified {@link RecipeType}
         */
        public <T extends ICustomRecipe<?, ?>> List<T> get(RecipeType<T> type) {
            return values().parallelStream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
        }

        public CraftingRecipe<?> getAdvancedCrafting(NamespacedKey recipeKey) {
            ICustomRecipe<?, ?> customRecipe = Registry.RECIPES.get(recipeKey);
            return customRecipe instanceof CraftingRecipe<?> craftingRecipe && craftingRecipe instanceof AdvancedCraftingRecipe ? craftingRecipe : null;
        }

        /**
         * Get all the recipes that are available.
         * Recipes that are hidden or disabled are not included.
         *
         * @return The recipes that are available and are not hidden or disabled.
         */
        public List<ICustomRecipe<?, ?>> getAvailable() {
            return getAvailable(values().parallelStream());
        }

        /**
         * Similar to {@link #getAvailable()} only includes the visible and enabled recipes, but also takes the player into account.
         * Recipes that the player has no permission to view are not included.
         *
         * @param player The player to get the recipes for.
         * @return The recipes that are available and the player has permission to view.
         */
        public List<ICustomRecipe<?, ?>> getAvailable(Player player) {
            return getAvailable(getAvailable(), player);
        }

        /**
         * Same as {@link #get(RecipeType)}, but only includes the visible and enabled recipes.
         *
         * @param type The type of the recipe.
         * @param <T>  The type passed via the {@link RecipeType}
         * @return A list only including the {@link ICustomRecipe}s of the specified {@link RecipeType}, which are enabled and visible.
         */
        public <T extends ICustomRecipe<?, ?>> List<T> getAvailable(RecipeType<T> type) {
            return getAvailable(get(type.getClazz()).parallelStream());
        }

        /**
         * Same as {@link #getAvailable(RecipeType)}, but additionally only includes recipes the player has permission to view.
         *
         * @param type   The type of the recipe.
         * @param <T>    The type passed via the {@link RecipeType}
         * @param player The player to get the recipes for.
         * @return A list only including the {@link ICustomRecipe}s of the specified {@link RecipeType}, which are enabled, visible and the Player has permission to view.
         */
        public <T extends ICustomRecipe<?, ?>> List<T> getAvailable(RecipeType<T> type, Player player) {
            return getAvailable(getAvailable(type), player);
        }

        /**
         * The same as {@link #getAvailable(Player)}, but only includes the recipes that contain the similar ItemStack in the result List.
         *
         * @param result The result ItemsStack to look for.
         * @param player The player to get the recipes for.
         * @return All the recipes, that have the specified Result, are not hidden, and the player has permission to view.
         */
        public List<ICustomRecipe<?, ?>> getAvailable(ItemStack result, Player player) {
            return getAvailable(player).parallelStream().filter(recipe -> recipe.findResultItem(result)).collect(Collectors.toList());
        }

        public synchronized <T extends ICustomRecipe<?, ?>> List<T> getAvailable(List<T> recipes, @Nullable Player player) {
            return getAvailable(recipes.stream().filter(recipe -> recipe.checkCondition("permission", new Conditions.Data(player))));
        }

        /**
         * Filters the Recipes stream from disabled or hidden recipes, and sorts the list according to the {@link me.wolfyscript.customcrafting.recipes.RecipePriority}!
         *
         * @param recipes The Stream of Recipes to filter.
         * @param <T>     The type of the {@link ICustomRecipe}
         * @return A filtered {@link List} containing only visible and enabled recipes.
         */
        private <T extends ICustomRecipe<?, ?>> List<T> getAvailable(Stream<T> recipes) {
            return recipes.filter(recipe -> !recipe.isHidden() && !recipe.isDisabled()).sorted(Comparator.comparing(ICustomRecipe::getPriority)).collect(Collectors.toList());
        }

        @Deprecated
        public Stream<CraftingRecipe<?>> getSimilar(List<List<ItemStack>> items, boolean elite, boolean advanced) {
            final long size = items.stream().flatMap(Collection::stream).filter(itemStack -> !ItemUtils.isAirOrNull(itemStack)).count();
            List<CraftingRecipe<?>> craftingRecipes = new ArrayList<>();
            if (elite) {
                craftingRecipes.addAll(get(Types.ELITE_WORKBENCH));
            }
            if (advanced) {
                craftingRecipes.addAll(get(Types.WORKBENCH));
            }
            final int itemsSize = items.size();
            final int items0Size = itemsSize > 0 ? items.get(0).size() : 0;
            return craftingRecipes.stream().filter(recipe -> {
                if (recipe instanceof AbstractShapedCraftRecipe<?> shapedRecipe) {
                    return itemsSize > 0 && shapedRecipe.getShape().length > 0 && items0Size == shapedRecipe.getShape()[0].length() && itemsSize == shapedRecipe.getShape().length;
                }
                return recipe.getIngredients().keySet().size() == size;
            }).sorted(Comparator.comparing(ICustomRecipe::getPriority));
        }

        public Stream<CraftingRecipe<?>> getSimilarCraftingRecipes(List<ItemStack> items, boolean elite, boolean advanced) {
            List<CraftingRecipe<?>> craftingRecipes = new ArrayList<>();
            if (elite) {
                craftingRecipes.addAll(get(Types.ELITE_WORKBENCH));
            }
            if (advanced) {
                craftingRecipes.addAll(get(Types.WORKBENCH));
            }
            final int size = items.size();
            final long strippedSize = items.stream().filter(itemStack -> !ItemUtils.isAirOrNull(itemStack)).count();
            return craftingRecipes.stream().filter(recipe -> recipe instanceof AbstractShapedCraftRecipe<?> shapedRecipe ? shapedRecipe.getFlatIngredients().size() == size : recipe.getIngredients().keySet().size() == strippedSize).sorted(Comparator.comparing(ICustomRecipe::getPriority));
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
