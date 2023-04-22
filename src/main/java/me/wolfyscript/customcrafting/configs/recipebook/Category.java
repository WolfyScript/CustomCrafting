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

package me.wolfyscript.customcrafting.configs.recipebook;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cache.CacheEliteCraftingTable;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonInclude;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@JsonPropertyOrder({"id", "icon", "name", "description", "auto"})
public class Category extends CategorySettings {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean auto = false;
    @JsonIgnore
    protected final List<RecipeContainer> containers;
    @JsonIgnore
    private final Map<CategoryFilter, List<RecipeContainer>> indexedFilters = new HashMap<>();
    private ContentSortation sort;

    public Category() {
        super();
        this.containers = new ObjectArrayList<>();
        this.title = getName();
    }

    public Category(Category category) {
        super(category);
        this.auto = category.auto;
        this.title = category.title;
        this.containers = new ObjectArrayList<>();
    }

    void index(CustomCrafting customCrafting, Collection<CategoryFilter> filters) {
        var registry = customCrafting.getRegistries().getRecipes();
        if (auto) {
            this.recipes.clear();
            this.folders.clear();
            this.groups.clear();
            this.groups.addAll(registry.groups());
            this.namespaces.clear();
            this.namespaces.addAll(registry.namespaces());
        }
        containers.clear();
        //Construct containers based on settings
        List<RecipeContainer> updatedContainers = new ObjectArrayList<>();
        this.recipes.stream().map(namespacedKey -> customCrafting.getRegistries().getRecipes().has(namespacedKey) ? new RecipeContainer(customCrafting, namespacedKey) : null).filter(Objects::nonNull).forEach(updatedContainers::add);
        this.groups.stream().map(group -> new RecipeContainer(customCrafting, group)).forEach(updatedContainers::add);
        this.folders.stream().flatMap(folder -> registry.get(NamespacedKeyUtils.NAMESPACE, folder).stream().map(customRecipe -> new RecipeContainer(customCrafting, customRecipe))).forEach(updatedContainers::add);
        this.namespaces.stream().flatMap(namespace -> registry.get(namespace).stream().map(customRecipe -> new RecipeContainer(customCrafting, customRecipe))).forEach(updatedContainers::add);
        containers.addAll(updatedContainers.stream().distinct().toList());
        sort.sortRecipeContainers(containers);
        //Index filters for quick filtering on runtime.
        filters.forEach(this::indexFilters);
    }

    public void indexFilters(CategoryFilter filter) {
        Preconditions.checkNotNull(filter, "Filter cannot be null! Cannot filter containers with null Filter!");
        List<RecipeContainer> filterContainers = containers.stream().filter(filter::filter).collect(Collectors.toList());
        filter.getSort().sortRecipeContainers(filterContainers);
        indexedFilters.put(filter, filterContainers);
    }

    public List<RecipeContainer> getRecipeList(Player player, CategoryFilter filter) {
        return getRecipeList(player, filter, null);
    }

    public List<RecipeContainer> getRecipeList(Player player, CategoryFilter filter, CacheEliteCraftingTable cacheEliteCraftingTable) {
        if (cacheEliteCraftingTable != null) {
            return getContainers(filter).stream().filter(container -> container.canView(player) && container.isValid(cacheEliteCraftingTable)).toList();
        }
        return getContainers(filter).stream().filter(container -> container.canView(player)).toList();
    }

    /**
     * Returns the containers that are filtered by the specified filter.<br>
     * In case no filter is provided or the category hasn't been filtered, then it returns the whole unfiltered list of recipe containers.
     *
     * @param filter The filter to get the containers for; or null to get an unfiltered list.
     * @return The list of filtered containers; or unfiltered list of containers.
     */
    private Collection<RecipeContainer> getContainers(@Nullable CategoryFilter filter) {
        return !indexedFilters.isEmpty() && filter != null ? indexedFilters.getOrDefault(filter, List.of()) : containers;
    }

    public void setSort(ContentSortation sort) {
        this.sort = sort;
    }

    public ContentSortation getSort() {
        return sort;
    }

    @JsonGetter("auto")
    public boolean isAuto() {
        return auto;
    }

    @JsonSetter("auto")
    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    @Override
    public void writeToByteBuf(MCByteBuf byteBuf) {
        super.writeToByteBuf(byteBuf);
        byteBuf.writeBoolean(this.auto);
        if (!auto) {
            writeData(byteBuf);
        }
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        if (title == null) { // Preserve old behaviour of the name
            title = getName();
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
