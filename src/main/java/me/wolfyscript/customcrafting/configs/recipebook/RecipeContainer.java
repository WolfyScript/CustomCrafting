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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cache.CacheEliteCraftingTable;
import me.wolfyscript.customcrafting.recipes.AbstractRecipeShapeless;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CraftingRecipeEliteShaped;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.EliteWorkbenchCondition;
import me.wolfyscript.customcrafting.registry.RegistryRecipes;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class RecipeContainer implements Comparable<RecipeContainer> {

    private final RegistryRecipes recipes;
    private final List<CustomRecipe<?>> cachedRecipes;
    //private final Map<UUID, List<ICustomRecipe<?, ?>>> cachedPlayerRecipes = new HashMap<>();
    private final Map<UUID, List<ItemStack>> cachedPlayerItemStacks = new HashMap<>();

    private final String group;
    private final NamespacedKey recipe;

    public RecipeContainer(CustomCrafting customCrafting, String group) {
        this.recipes = customCrafting.getRegistries().getRecipes();
        this.group = group;
        this.recipe = null;
        this.cachedRecipes = recipes.getGroup(group);
    }

    public RecipeContainer(CustomCrafting customCrafting, NamespacedKey recipe) {
        this.recipes = customCrafting.getRegistries().getRecipes();
        this.group = null;
        this.recipe = recipe;
        this.cachedRecipes = Collections.singletonList(recipes.get(recipe));
    }

    public RecipeContainer(CustomCrafting customCrafting, CustomRecipe<?> recipe) {
        this.recipes = customCrafting.getRegistries().getRecipes();
        this.group = null;
        this.recipe = recipe.getNamespacedKey();
        this.cachedRecipes = Collections.singletonList(recipe);
    }

    /**
     * @param player The player to get the recipes for.
     * @return The recipes of this container the player has access to.
     */
    public List<CustomRecipe<?>> getRecipes(Player player) {
        return recipes.getAvailable(cachedRecipes, player); //Possible strict caching in the future?! return cachedPlayerRecipes.computeIfAbsent(player.getUniqueId(), uuid -> Registry.RECIPES.getAvailable(cachedRecipes, player));
    }

    /**
     * Checks if a player can view at least one recipe of this container.
     *
     * @param player The player to check.
     * @return True if the player can view the container.
     */
    public boolean canView(Player player) {
        return cachedRecipes.stream().anyMatch(recipe1 -> !recipe1.isHidden() && !recipe1.isDisabled() && recipe1.checkCondition("permission", Conditions.Data.of(player)));
    }

    public @Nullable String getGroup() {
        return group;
    }

    public @Nullable NamespacedKey getRecipe() {
        return recipe;
    }

    public boolean isValid(Set<Material> materials) {
        return materials.isEmpty() || cachedRecipes.stream().anyMatch(recipe1 -> recipe1.getResult().getChoices().stream().anyMatch(customItem -> materials.contains(customItem.getItemStack().getType())));
    }

    public boolean isValid(CacheEliteCraftingTable cacheEliteCraftingTable) {
        var data = cacheEliteCraftingTable.getData();
        return cachedRecipes.parallelStream().anyMatch(cachedRecipe -> {
            if (cachedRecipe instanceof CraftingRecipe<?, ?> && (RecipeType.Container.ELITE_CRAFTING.isInstance(cachedRecipe) || data.isAdvancedRecipes())) {
                if (RecipeType.Container.ELITE_CRAFTING.isInstance(cachedRecipe)) {
                    Conditions conditions = cachedRecipe.getConditions();
                    if (conditions.has(EliteWorkbenchCondition.KEY) && !conditions.getByType(EliteWorkbenchCondition.class).getEliteWorkbenches().contains(cacheEliteCraftingTable.getCustomItem().getNamespacedKey())) {
                        return false;
                    }
                    if (cachedRecipe instanceof AbstractRecipeShapeless<?, ?> shapeless) {
                        return shapeless.getIngredients().size() <= cacheEliteCraftingTable.getCurrentGridSize() * cacheEliteCraftingTable.getCurrentGridSize();
                    } else {
                        CraftingRecipeEliteShaped recipe1 = (CraftingRecipeEliteShaped) cachedRecipe;
                        return recipe1.getShape().length <= cacheEliteCraftingTable.getCurrentGridSize() && recipe1.getShape()[0].length() <= cacheEliteCraftingTable.getCurrentGridSize();
                    }
                }
                return true;
            }
            return false;
        });
    }

    public String getValue() {
        return group != null ? group : recipe.toString();
    }

    public ItemStack getDisplayItem() {
        return cachedRecipes.isEmpty() ? new ItemStack(Material.STONE) : cachedRecipes.get(0).getRecipeBookItems().get(0).create();
    }

    public List<ItemStack> getDisplayItems(Player player) {
        return cachedPlayerItemStacks.computeIfAbsent(player.getUniqueId(), uuid -> getRecipes(player).stream().flatMap(recipe1 -> recipe1.getRecipeBookItems().stream()).map(CustomItem::create).distinct().toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeContainer that = (RecipeContainer) o;
        return Objects.equals(group, that.group) && Objects.equals(recipe, that.recipe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, recipe);
    }

    @Override
    public int compareTo(@NotNull RecipeContainer container) {
        return getValue().compareTo(container.getValue());
    }

    @Override
    public String toString() {
        return "RecipeContainer{" +
                "group='" + group + '\'' +
                ", recipe=" + recipe +
                '}';
    }
}
