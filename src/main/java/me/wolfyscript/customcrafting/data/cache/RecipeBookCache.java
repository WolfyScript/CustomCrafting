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

package me.wolfyscript.customcrafting.data.cache;

import java.util.Optional;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeBookCache {

    private final CustomCrafting customCrafting;
    private CacheEliteCraftingTable eliteCraftingTable;
    private Category category;
    private CategoryFilter categoryFilter;

    private int page;
    private int subFolderPage;
    private Map<CustomItem, List<CustomRecipe<?>>> cachedSubFolderRecipes;
    private List<CustomItem> researchItems;

    private boolean prepareRecipe;

    public RecipeBookCache(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.page = 0;
        this.subFolderPage = 0;
        this.category = null;
        this.categoryFilter = null;
        this.researchItems = new ArrayList<>();
        this.cachedSubFolderRecipes = new HashMap<>();
        this.eliteCraftingTable = null;
        this.prepareRecipe = true;
    }

    public CustomRecipe<?> getCurrentRecipe() {
        if (getSubFolderPage() >= 0 && getSubFolderPage() < getSubFolderRecipes().size()) {
            return getSubFolderRecipes().get(getSubFolderPage());
        }
        return null;
    }

    public void setPrepareRecipe(boolean prepareRecipe) {
        this.prepareRecipe = prepareRecipe;
    }

    public boolean isPrepareRecipe() {
        return prepareRecipe;
    }

    public CacheEliteCraftingTable getEliteCraftingTable() {
        return eliteCraftingTable;
    }

    public void setEliteCraftingTable(CacheEliteCraftingTable eliteCraftingTable) {
        this.eliteCraftingTable = eliteCraftingTable;
    }

    public boolean hasEliteCraftingTable() {
        return getEliteCraftingTable() != null;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @NotNull
    public Optional<CategoryFilter> getCategoryFilter() {
        return Optional.ofNullable(categoryFilter != null ? categoryFilter : customCrafting.getConfigHandler().getRecipeBookConfig().getFilter(0));
    }

    public void setCategoryFilter(CategoryFilter categoryFilter) {
        this.categoryFilter = categoryFilter;
    }

    public Map<Character, ArrayList<CustomItem>> getIngredients() {
        return new HashMap<>();
    }

    public int getSubFolder() {
        return researchItems.size();
    }

    public List<CustomRecipe<?>> getSubFolderRecipes() {
        return this.cachedSubFolderRecipes.getOrDefault(getResearchItem(), new ArrayList<>());
    }

    public void setSubFolderRecipes(CustomItem customItem, List<CustomRecipe<?>> subFolderRecipes) {
        this.cachedSubFolderRecipes.put(customItem, subFolderRecipes);
    }

    public int getSubFolderPage() {
        return subFolderPage;
    }

    public void setSubFolderPage(int subFolderPage) {
        this.subFolderPage = subFolderPage;
    }

    public List<CustomItem> getResearchItems() {
        return researchItems;
    }

    public void addResearchItem(CustomItem item) {
        researchItems.add(0, item);
    }

    public void removePreviousResearchItem() {
        if (!researchItems.isEmpty()) {
            researchItems.remove(0);
        }
    }

    public void setResearchItems(List<CustomItem> researchItems) {
        this.researchItems = researchItems;
    }

    public CustomItem getResearchItem() {
        return getResearchItems().get(0);
    }

    public void applyRecipeToButtons(GuiHandler<CCCache> guiHandler, CustomRecipe<?> recipe) {
        recipe.prepareMenu(guiHandler, guiHandler.getInvAPI().getGuiCluster("recipe_book"));
    }

    public void setCachedSubFolderRecipes(Map<CustomItem, List<CustomRecipe<?>>> cachedSubFolderRecipes) {
        this.cachedSubFolderRecipes = cachedSubFolderRecipes;
    }
}
