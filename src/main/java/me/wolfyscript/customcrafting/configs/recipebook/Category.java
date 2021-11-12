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

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.entity.Player;

import java.util.*;

@JsonPropertyOrder({"id", "icon", "name", "description", "auto"})
public class Category extends CategorySettings {

    protected final List<RecipeContainer> containers;
    private final Map<CategoryFilter, List<RecipeContainer>> indexedFilters = new HashMap<>();
    private boolean auto;

    public Category() {
        super();
        this.containers = new ArrayList<>();
        this.auto = recipes.isEmpty() && namespaces.isEmpty();
    }

    public Category(Category category) {
        super(category);
        this.auto = category.auto;
        this.containers = new ArrayList<>();
    }

    void index(Collection<CategoryFilter> filters) {
        if (auto) {
            this.namespaces.clear();
            this.groups.clear();
            this.namespaces.addAll(CCRegistry.RECIPES.namespaces());
            this.groups.addAll(CCRegistry.RECIPES.groups());
        }
        containers.clear();
        //Construct containers based on settings
        List<RecipeContainer> recipeContainers = new ArrayList<>();
        recipeContainers.addAll(this.groups.stream().map(RecipeContainer::new).toList());
        recipeContainers.addAll(this.namespaces.stream().flatMap(s -> CCRegistry.RECIPES.get(s).stream().filter(recipe -> recipe.getGroup().isEmpty() || !groups.contains(recipe.getGroup())).map(RecipeContainer::new)).toList());
        recipeContainers.addAll(this.recipes.stream().map(namespacedKey -> {
            CustomRecipe<?> recipe = CCRegistry.RECIPES.get(namespacedKey);
            return recipe == null ? null : new RecipeContainer(recipe);
        }).filter(Objects::nonNull).toList());
        containers.addAll(recipeContainers.stream().distinct().sorted().toList());
        //Index filters for quick filtering on runtime.
        filters.forEach(this::indexFilters);
    }

    public void indexFilters(CategoryFilter filter) {
        indexedFilters.put(filter, containers.stream().filter(filter::filter).toList());
    }

    public List<RecipeContainer> getRecipeList(Player player, CategoryFilter filter) {
        return getRecipeList(player, filter, null);
    }

    public List<RecipeContainer> getRecipeList(Player player, CategoryFilter filter, EliteWorkbench eliteWorkbench) {
        if (eliteWorkbench != null) {
            return getContainers(filter).stream().filter(container -> container.canView(player) && container.isValid(eliteWorkbench)).toList();
        }
        return getContainers(filter).stream().filter(container -> container.canView(player)).toList();
    }

    private List<RecipeContainer> getContainers(CategoryFilter filter) {
        return indexedFilters.getOrDefault(filter, new ArrayList<>());
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
}
