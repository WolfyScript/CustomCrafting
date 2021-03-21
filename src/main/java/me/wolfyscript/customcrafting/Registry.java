package me.wolfyscript.customcrafting;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.types.IShapedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.customcrafting.utils.recipe_item.extension.ResultExtension;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
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
    ResultExtensionRegistry RESULT_EXTENSIONS = new ResultExtensionRegistry();

    /**
     * The custom Registry for the Recipes of CustomCrafting.
     * Providing a lot of functionality to get the recipes you need.
     */
    class RecipeRegistry extends me.wolfyscript.utilities.util.Registry.SimpleRegistry<ICustomRecipe<?, ?>> {

        public RecipeRegistry() {
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
            remove(namespacedKey);
            super.register(namespacedKey, value);
            if (value instanceof ICustomVanillaRecipe) {
                Bukkit.addRecipe(((ICustomVanillaRecipe<?>) value).getVanillaRecipe());
            }
        }

        public void register(ICustomRecipe<?, ?> value) {
            this.register(value.getNamespacedKey(), value);
        }

        public List<String> namespaces() {
            return keySet().parallelStream().map(NamespacedKey::getNamespace).distinct().collect(Collectors.toList());
        }

        /**
         * Returns a List of all recipes contained in the namespace.
         *
         * @param namespace
         * @return
         */
        public List<ICustomRecipe<?, ?>> get(String namespace) {
            return entrySet().parallelStream().filter(entry -> entry.getKey().getNamespace().equalsIgnoreCase(namespace)).map(Map.Entry::getValue).collect(Collectors.toList());
        }

        /**
         * Get all the Recipes from this group
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

        public <T extends ICustomRecipe<?, ?>> List<T> get(RecipeType<T> type) {
            return get(type.getClazz());
        }

        public AdvancedCraftingRecipe getAdvancedCrafting(NamespacedKey recipeKey) {
            ICustomRecipe<?, ?> customRecipe = Registry.RECIPES.get(recipeKey);
            return customRecipe instanceof AdvancedCraftingRecipe ? (AdvancedCraftingRecipe) customRecipe : null;
        }

        /**
         * Get all the recipes that are available.
         * Recipes that are hidden or disabled are not included.
         *
         * @return
         */
        public List<ICustomRecipe<?, ?>> getAvailable() {
            return getAvailable(values().parallelStream());
        }

        /**
         * Similar to {@link #getAvailable()} only includes the visible and enabled recipes, but also takes the player into account.
         * Recipes that the player has no permission to view are not included.
         *
         * @param player
         * @return
         */
        public List<ICustomRecipe<?, ?>> getAvailable(Player player) {
            return getAvailable(getAvailable(), player);
        }

        public <T extends ICustomRecipe<?, ?>> List<T> getAvailable(RecipeType<T> type) {
            return getAvailable(get(type.getClazz()).parallelStream());
        }

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

        private <T extends ICustomRecipe<?, ?>> List<T> getAvailable(Stream<T> recipes) {
            return recipes.filter(recipe -> !recipe.isHidden() && !CustomCrafting.getInst().getRecipeHandler().getDisabledRecipes().contains(recipe.getNamespacedKey())).sorted(Comparator.comparing(ICustomRecipe::getPriority)).collect(Collectors.toList());
        }

        synchronized public <T extends ICustomRecipe<?, ?>> List<T> getAvailable(List<T> recipes, @Nullable Player player) {
            return recipes.stream().filter(recipe -> recipe.getConditions().getByID("permission") == null || recipe.getConditions().getByID("permission").check(recipe, new Conditions.Data(player, null, null))).sorted(Comparator.comparing(ICustomRecipe::getPriority)).collect(Collectors.toList());
        }

        public Stream<CraftingRecipe<?>> getSimilar(List<List<ItemStack>> items, boolean elite, boolean advanced) {
            final long size = items.stream().flatMap(Collection::parallelStream).filter(itemStack -> !ItemUtils.isAirOrNull(itemStack)).count();
            List<CraftingRecipe<?>> craftingRecipes = new ArrayList<>();
            if (elite) {
                craftingRecipes.addAll(get(Types.ELITE_WORKBENCH));
            }
            if (advanced) {
                craftingRecipes.addAll(get(Types.WORKBENCH));
            }
            final int itemsSize = items.size();
            final int items0Size = itemsSize > 0 ? items.get(0).size() : 0;
            return craftingRecipes.stream().filter(r -> r.getIngredients().keySet().size() == size).filter(recipe -> {
                if (recipe instanceof IShapedCraftingRecipe) {
                    IShapedCraftingRecipe shapedRecipe = ((IShapedCraftingRecipe) recipe);
                    return itemsSize > 0 && shapedRecipe.getHeight() > 0 && itemsSize == shapedRecipe.getHeight() && items0Size == shapedRecipe.getWidth();
                }
                return true;
            }).sorted(Comparator.comparing(ICustomRecipe::getPriority));
        }


        public int size() {
            return this.map.size();
        }

        private void removeBukkitRecipe(NamespacedKey namespacedKey) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_15)) {
                Bukkit.removeRecipe(namespacedKey.toBukkit());
            } else {
                Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
                boolean inject = false;
                while (recipeIterator.hasNext()) {
                    Recipe recipe = recipeIterator.next();
                    if (((Keyed) recipe).getKey().toString().equals(namespacedKey.toString())) {
                        if (!inject) {
                            inject = true;
                        }
                        recipeIterator.remove();
                    }
                }
                if (inject) {
                    Bukkit.resetRecipes();
                    while (recipeIterator.hasNext()) {
                        Bukkit.addRecipe(recipeIterator.next());
                    }
                }
            }
        }
    }

    class ResultExtensionRegistry extends SimpleRegistry<ResultExtension.Provider<?>> {

        public void register(ResultExtension value) {
            super.register(new ResultExtension.Provider<>(value.getNamespacedKey(), value.getClass()));
        }

    }

}
