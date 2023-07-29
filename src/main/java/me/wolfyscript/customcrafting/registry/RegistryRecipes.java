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

package me.wolfyscript.customcrafting.registry;


import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.registry.Registries;
import me.wolfyscript.utilities.registry.RegistrySimple;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The custom Registry for the Recipes of CustomCrafting.
 * Providing a lot of functionality to get the recipes you need.
 */
public final class RegistryRecipes extends RegistrySimple<CustomRecipe<?>> {

    private final CustomCrafting customCrafting;

    private final Map<String, List<CustomRecipe<?>>> BY_NAMESPACE = new HashMap<>();
    private final Map<String, List<CustomRecipe<?>>> BY_GROUP = new HashMap<>();
    private final Map<CustomItem, List<CustomRecipe<?>>> BY_RESULT = new HashMap<>();
    private final Map<Class<?>, List<CustomRecipe<?>>> BY_CLASS_TYPE = new HashMap<>();
    private final Map<RecipeType<?>, List<CustomRecipe<?>>> BY_RECIPE_TYPE = new HashMap<>();
    private final Map<RecipeType.Container<?>, List<CustomRecipe<?>>> BY_RECIPE_TYPE_CONTAINER = new HashMap<>();
    private final Map<String, Map<String, List<CustomRecipe<?>>>> BY_NAMESPACE_AND_FOLDER = new HashMap<>();
    private final Map<String, Map<String, List<CustomRecipe<?>>>> BY_NAMESPACE_AND_DIR = new HashMap<>();
    private final Set<String> NAMESPACES = new HashSet<>();
    private final Map<String, List<String>> FOLDERS = new HashMap<>();
    private final Set<String> GROUPS = new HashSet<>();

    RegistryRecipes(CustomCrafting customCrafting, Registries registries) {
        super(new NamespacedKey(customCrafting, "recipe/recipes"), registries);
        this.customCrafting = customCrafting;
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
        FOLDERS.remove(key.getNamespace());
        GROUPS.clear();
        BY_RESULT.clear();
        BY_CLASS_TYPE.clear();
        BY_RECIPE_TYPE.clear();
        BY_RECIPE_TYPE_CONTAINER.clear();
        BY_NAMESPACE_AND_FOLDER.remove(key.getNamespace());
        BY_NAMESPACE_AND_DIR.remove(key.getNamespace());
    }

    @Override
    public synchronized void register(NamespacedKey namespacedKey, CustomRecipe<?> value) {
        Preconditions.checkArgument(namespacedKey != null, "Invalid NamespacedKey! The namespaced key cannot be null!");
        Preconditions.checkArgument(!namespacedKey.getNamespace().equalsIgnoreCase("minecraft"), "Invalid NamespacedKey! Cannot register recipe under minecraft namespace!");
        remove(namespacedKey);
        super.register(namespacedKey, value);
        if (value instanceof ICustomVanillaRecipe<?> vanillaRecipe && !value.isDisabled()) {
            try {
                Recipe bukkitRecipe = vanillaRecipe.getVanillaRecipe();
                if (bukkitRecipe != null && !Bukkit.addRecipe(bukkitRecipe)) {
                    customCrafting.getLogger().warning(String.format("Didn't add recipe '%s' to Bukkit! Most likely already exists!", namespacedKey));
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                customCrafting.getLogger().warning(String.format("Failed to add recipe '%s' to Bukkit: %s", namespacedKey, ex.getMessage()));
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
        if (NAMESPACES.isEmpty()) {
            NAMESPACES.addAll(keySet().stream().map(NamespacedKey::getNamespace).distinct().toList());
        }
        return new ArrayList<>(NAMESPACES);
    }

    /**
     * Gets the folders available in the specified namespace.<br>
     * This will return a tree list of the available folders separated by "/".<br>
     * For example:<br>
     * A single namespaced key like this:
     * <pre>&lt;namespace&gt;:&lt;root_folder&gt;/&lt;folder&gt;/&lt;sub_folder&gt;/&lt;recipe_name&gt;</pre>
     * will result in
     * <pre>["", "root_folder", "root_folder/folder", "root_folder/folder/sub_folder"]</pre>
     *
     * @param namespace The namespace to index the folders for.
     * @return A list of all available folders and sub folders.
     * @deprecated Replaced by {@link #dirs(String)}
     */
    @Deprecated
    public List<String> folders(String namespace) {
        return FOLDERS.computeIfAbsent(namespace, s -> {
            Set<String> folders = new HashSet<>();
            folders.add("");
            for (CustomRecipe<?> recipe : get(s)) {
                String[] parts = recipe.getNamespacedKey().getKey().split("/");
                if (parts.length > 0) {
                    StringBuilder path = new StringBuilder(parts[0]);
                    folders.add(path.toString());
                    for (int i = 1; i < parts.length - 1; i++) {
                        folders.add(path.append("/").append(parts[i]).toString());
                    }
                }
            }
            return List.copyOf(folders);
        });
    }

    /**
     * Gets the directories available in the specified namespace.<br>
     * This will return a list of the available directories separated by "/".<br>
     * <br>
     * For example:<br>
     * A registry with a single namespaced key like this:
     * <pre>&lt;namespace&gt;:&lt;root_dir&gt;/&lt;sec_dir&gt;/&lt;third_dir&gt;/&lt;recipe_name&gt;</pre>
     * will result in
     * <pre>
     * [
     *  "/",
     *  "/&lt;root_dir&gt;/",
     *  "/&lt;root_dir&gt;/&lt;sec_dir&gt;/",
     *  "/&lt;root_dir&gt;/&lt;sec_dir&gt;/&lt;third_dir&gt;/"
     * ]</pre>
     *
     * @param namespace The namespace to index the folders for.
     * @return A list of all available folders and sub folders.
     */
    public List<String> dirs(String namespace) {
        return dirs(namespace, 64);
    }

    /**
     * Gets the directories available in the specified namespace.<br>
     * This will return a list of the available directories separated by "/".<br>
     * <br>
     * For example:<br>
     * A registry with a single namespaced key like this:
     * <pre>&lt;namespace&gt;:&lt;root_dir&gt;/&lt;sec_dir&gt;/&lt;third_dir&gt;/&lt;recipe_name&gt;</pre>
     * will result in
     * <pre>
     * [
     *  "/",
     *  "/&lt;root_dir&gt;/",
     *  "/&lt;root_dir&gt;/&lt;sec_dir&gt;/",
     *  "/&lt;root_dir&gt;/&lt;sec_dir&gt;/&lt;third_dir&gt;/"
     * ]</pre>
     * <br>
     * The <b>max depth</b> limits how many levels it goes down the directories.<br>
     * So a max depth of 2 will return:<br>
     * <pre>
     * [
     * "/",
     * "/&lt;root_dir&gt;/",
     * "/&lt;root_dir&gt;/&lt;sec_dir&gt;/"
     * ]</pre>
     *
     * @param namespace The namespace to index the folders for.
     * @param maxDepth  The max depth of directory levels.
     * @return A list of all available folders and sub folders.
     */
    public List<String> dirs(String namespace, int maxDepth) {
        return dirs(namespace, maxDepth, true);
    }

    /**
     * Gets the directories available in the specified namespace.<br>
     * This will return a list of the available directories separated by "/".<br>
     * <br>
     * A registry with a single namespaced key like this:
     * <pre>&lt;namespace&gt;:&lt;root_dir&gt;/&lt;sec_dir&gt;/&lt;third_dir&gt;/&lt;recipe_name&gt;</pre>
     * will result in:<br>
     *
     * <pre>
     * [
     *  "/",
     *  "/&lt;root_dir&gt;/",
     *  "/&lt;root_dir&gt;/&lt;sec_dir&gt;/",
     *  "/&lt;root_dir&gt;/&lt;sec_dir&gt;/&lt;third_dir&gt;/"
     * ]</pre>
     * or
     * <pre>
     * [
     *  "&lt;root_dir&gt;/",
     *  "&lt;root_dir&gt;/&lt;sec_dir&gt;/",
     *  "&lt;root_dir&gt;/&lt;sec_dir&gt;/&lt;third_dir&gt;/"
     * ]</pre>
     * when <b>includeRoot</b> is <b>false</b>
     * <br>
     * The <b>max depth</b> limits how many levels it goes down the directories.<br>
     * So a max depth of 2 will return:<br>
     * <pre>
     * [
     * "/",
     * "/&lt;root_dir&gt;/",
     * "/&lt;root_dir&gt;/&lt;sec_dir&gt;/"
     * ]</pre>
     *
     * @param namespace   The namespace to index the folders for.
     * @param maxDepth    The max depth of directory levels.
     * @param includeRoot Specifies if the root '/' should be included.
     * @return A list of all available folders and sub folders.
     */
    public List<String> dirs(String namespace, int maxDepth, boolean includeRoot) {
        final String root = includeRoot ? "/" : "";
        return get(namespace).stream().<String>mapMulti((customRecipe, consumer) -> {
            String[] parts = customRecipe.getNamespacedKey().getKeyComponent().getFolder().split("/");
            if (includeRoot) consumer.accept(root);
            StringBuilder path = new StringBuilder(root);
            for (int i = 0; i < parts.length && i < maxDepth; i++) {
                consumer.accept(path.append(parts[i]).append("/").toString());
            }
        }).distinct().toList();
    }

    /**
     * Gets the subdirectories available in the specified namespace and directory.<br>
     * This will return a list of the available directories separated by "/".<br>
     * <br>
     * A registry with a single namespaced key like this:
     * <pre>&lt;namespace&gt;:&lt;root_dir&gt;/&lt;sec_dir&gt;/&lt;third_dir&gt;/&lt;recipe_name&gt;</pre>
     * will result in:<br>
     *
     * <pre>
     * [
     *  "/",
     *  "/&lt;root_dir&gt;/",
     *  "/&lt;root_dir&gt;/&lt;sec_dir&gt;/",
     *  "/&lt;root_dir&gt;/&lt;sec_dir&gt;/&lt;third_dir&gt;/"
     * ]</pre>
     * or
     * <pre>
     * [
     *  "&lt;root_dir&gt;/",
     *  "&lt;root_dir&gt;/&lt;sec_dir&gt;/",
     *  "&lt;root_dir&gt;/&lt;sec_dir&gt;/&lt;third_dir&gt;/"
     * ]</pre>
     * when <b>includeRoot</b> is <b>false</b>
     * <br>
     *
     * @param namespace The namespace to index the folders for.
     * @return A list of all available folders and sub folders.
     */
    public List<String> dirs(String namespace, final String directory, boolean includeRoot) {
        boolean hasRoot = directory.startsWith("/");
        // Clear the folder, so it is in proper format.
        final String dir = (
                includeRoot ?
                        (!hasRoot ? "/" + directory : directory) :
                        (hasRoot ? directory.substring(1) : directory)
        ) + (!directory.endsWith("/") ? "/" : "");
        return dirs(namespace, 64, includeRoot).stream().filter(sub -> sub.startsWith(dir) && (sub.length() != dir.length() || includeRoot)).toList();
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
     * Gets all recipes from the specified namespace and folder.
     *
     * @param namespace The namespace of the recipes.
     * @param dir       The folder of the recipes.
     * @return A list of all recipes in the folder inside the namespace.
     */
    public List<CustomRecipe<?>> getFromDir(String namespace, String dir) {
        dir = cleanDir(dir);
        return BY_NAMESPACE_AND_DIR.computeIfAbsent(namespace, s -> {
            Map<String, List<CustomRecipe<?>>> folderIndex = new HashMap<>();
            get(s).forEach(recipe -> {
                String key = recipe.getNamespacedKey().getKey();
                String recipeFolder = "/" + (key.contains("/") ? key.substring(0, key.lastIndexOf("/") + 1) : "");
                folderIndex.computeIfAbsent(recipeFolder, s1 -> new LinkedList<>()).add(recipe);
            });
            return folderIndex;
        }).getOrDefault(dir, new LinkedList<>());
    }

    private String cleanDir(String dir) {
        // Clear the folder, so it is in proper format.
        return (!dir.startsWith("/") ? "/" : "") + dir + (!dir.endsWith("/") ? "/" : "");
    }

    /**
     * Gets all recipes from the specified namespace and folder.
     *
     * @param namespace The namespace of the recipes.
     * @param folder    The folder of the recipes.
     * @return A list of all recipes in the folder inside the namespace.
     */
    public List<CustomRecipe<?>> get(String namespace, String folder) {
        return getFromDir(namespace, folder);
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
        return (List<T>) BY_CLASS_TYPE.computeIfAbsent(type, aClass -> values().stream().filter(aClass::isInstance).map(recipe -> ((Class<T>) aClass).cast(recipe)).collect(Collectors.toList()));
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

    @SafeVarargs
    public final <T extends CustomRecipe<?>> Stream<? extends T> get(RecipeType.Container<? extends T>... types) {
        return Arrays.stream(types).filter(Objects::nonNull).flatMap(container -> get(container).stream());
    }

    public CraftingRecipe<?, AdvancedRecipeSettings> getAdvancedCrafting(NamespacedKey recipeKey) {
        CustomRecipe<?> customRecipe = get(recipeKey);
        return RecipeType.Container.CRAFTING.isInstance(customRecipe) ? RecipeType.Container.CRAFTING.cast(customRecipe) : null;
    }

    /**
     * Filters the Recipes stream from disabled or hidden recipes, and sorts the list according to the {@link me.wolfyscript.customcrafting.recipes.RecipePriority}!
     *
     * @param recipes The Stream of Recipes to filter.
     * @param <T>     The type of the {@link CustomRecipe}
     * @return A filtered {@link List} containing only visible and enabled recipes.
     */
    public <T extends CustomRecipe<?>> Stream<T> filterAvailable(Stream<T> recipes) {
        return recipes.filter(recipe -> !recipe.isHidden() && !recipe.isDisabled());
    }

    /**
     * Get all the recipes that are available.
     * Recipes that are hidden or disabled are not included.
     *
     * @return The recipes that are available and are not hidden or disabled.
     */
    public List<CustomRecipe<?>> getAvailable() {
        return filterAvailable(values().stream()).sorted().toList();
    }

    /**
     * Similar to {@link #getAvailable()} only includes the visible and enabled recipes, but also takes the player into account.
     * Recipes that the player has no permission to view are not included.
     *
     * @param player The player to get the recipes for.
     * @return The recipes that are available and the player has permission to view.
     */
    @Deprecated
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
        return filterAvailable(get(type.getRecipeClass()).stream()).sorted().toList();
    }

    /**
     * Same as {@link #getAvailable(RecipeType)}, but additionally only includes recipes the player has permission to view.
     *
     * @param type   The type of the recipe.
     * @param <T>    The type passed via the {@link RecipeType}
     * @param player The player to get the recipes for.
     * @return A list only including the {@link CustomRecipe}s of the specified {@link RecipeType}, which are enabled, visible and the Player has permission to view.
     */
    @Deprecated
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
    @Deprecated
    public List<CustomRecipe<?>> getAvailable(ItemStack result, Player player) {
        return getAvailable(player).stream().filter(recipe -> recipe.findResultItem(result)).collect(Collectors.toList());
    }

    @Deprecated
    public synchronized <T extends CustomRecipe<?>> List<T> getAvailable(List<T> recipes, @Nullable Player player) {
        return filterAvailable(recipes.stream()).filter(recipe -> recipe.checkCondition("permission", Conditions.Data.of(player))).sorted().toList();
    }

    public int size() {
        return this.map.size();
    }

    private void removeBukkitRecipe(NamespacedKey namespacedKey) {
        Bukkit.removeRecipe(new org.bukkit.NamespacedKey(namespacedKey.getNamespace(), namespacedKey.getKey()));
    }
}
