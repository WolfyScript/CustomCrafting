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

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public class ContentSortation {

    private final DefaultSortAlgo defaultSortAlgo;
    private final Order order;
    @JsonIgnore private final Object2IntMap<NamespacedKey> recipeOrdering;
    @JsonIgnore private final Object2IntMap<String> groupOrdering;

    public static final Comparator<RecipeContainer> ID_GROUP_FIRST = (o1, o2) -> {
        if (o1.getGroup() != null) {
            if (o2.getGroup() != null) return o1.getGroup().compareTo(o2.getGroup());
            return -1;
        } else if (o2.getGroup() != null) return 1;
        assert o1.getRecipe() != null && o2.getRecipe() != null;
        return o1.getRecipe().compareTo(o2.getRecipe());
    };
    public static final Comparator<RecipeContainer> ID_RECIPE_FIRST = (o1, o2) -> {
        if (o1.getRecipe() != null && o2.getRecipe() != null) {
            if (o2.getRecipe() != null) return o1.getRecipe().compareTo(o2.getRecipe());
            return -1;
        } else if (o2.getRecipe() != null) return 1;
        assert o1.getGroup() != null && o2.getGroup() != null;
        return o1.getGroup().compareTo(o2.getGroup());
    };

    @JsonCreator
    public ContentSortation(@JsonProperty("defaultSort") DefaultSortAlgo defaultSortAlgo,
                            @JsonProperty("order") Order order) {
        this.defaultSortAlgo = defaultSortAlgo;
        this.order = order;
        this.recipeOrdering = new Object2IntOpenHashMap<>();
        this.groupOrdering = new Object2IntOpenHashMap<>();
    }

    @JsonSetter("recipes")
    public void addRecipeOrdering(Map<NamespacedKey, Integer> recipeOrdering) {
        this.recipeOrdering.putAll(recipeOrdering);
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonGetter("recipes")
    private Map<NamespacedKey, Integer> getRecipeOrder() {
        return this.recipeOrdering;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonSetter("groups")
    public void addGroupOrdering(Map<String, Integer> groupOrdering) {
        this.groupOrdering.putAll(groupOrdering);
    }

    @JsonGetter("groups")
    private Map<String, Integer> getGroupOrder() {
        return this.groupOrdering;
    }

    public DefaultSortAlgo getDefaultSortAlgo() {
        return defaultSortAlgo;
    }

    public Order getOrder() {
        return order;
    }

    @JsonIgnore
    public Object2IntMap<NamespacedKey> getRecipeOrdering() {
        return recipeOrdering;
    }

    @JsonIgnore
    public Object2IntMap<String> getGroupOrdering() {
        return groupOrdering;
    }

    @JsonIgnore
    public void sortRecipeContainers(List<RecipeContainer> containers) {
        Comparator<RecipeContainer> comparator = switch (getDefaultSortAlgo()) {
            case NONE -> /* Keep the insertion order */ null;
            case ID -> Comparator.naturalOrder();
            case ID_GROUPS_FIRST -> ContentSortation.ID_GROUP_FIRST;
            case ID_RECIPES_FIRST -> ContentSortation.ID_RECIPE_FIRST;
        };
        if (comparator != null) {
            if (getOrder() == Order.DESCENDING) {
                comparator = comparator.reversed();
            }
            containers.sort(comparator);
        }

        Comparator<RecipeContainer> customComparator = (o1, o2) -> {
            int o1Priority = o1.getGroup() != null ? getGroupOrdering().getOrDefault(o1.getGroup(), 0) : getRecipeOrdering().getOrDefault(o1.getRecipe(), 0);
            int o2Priority = o2.getGroup() != null ? getGroupOrdering().getOrDefault(o2.getGroup(), 0) : getRecipeOrdering().getOrDefault(o2.getRecipe(), 0);
            return Integer.compare(o1Priority, o2Priority);
        };
        if (getOrder() == Order.DESCENDING) {
            customComparator = customComparator.reversed();
        }
        containers.sort(customComparator);
    }

    public enum Order {

        ASCENDING,
        DESCENDING

    }

    public enum DefaultSortAlgo {
        /**
         * Keeps the order in which recipes/groups were added to the recipe book.
         */
        NONE,
        /**
         * Treats recipe and group ids the same way and uses Strings natural ordering.
         */
        ID,
        /**
         * First sorts recipes on their own and then appends the sorted groups.
         */
        ID_RECIPES_FIRST,
        /**
         * First sorts groups on their own and then appends the sorted recipes.
         */
        ID_GROUPS_FIRST

    }

}
