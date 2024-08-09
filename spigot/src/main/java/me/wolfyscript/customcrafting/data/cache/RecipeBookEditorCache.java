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

import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.configs.recipebook.CategorySettings;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBookConfig;

import java.util.regex.Pattern;

public class RecipeBookEditorCache {

    private static final Pattern VALID_ID = Pattern.compile("[a-z0-9/._-]+");
    private boolean switchCategories;

    private String categoryID;
    private Category category;
    private CategoryFilter categoryFilter;

    private int categoriesPage = 0;
    private int filtersPage = 0;

    private RecipeBookConfig editorConfigCopy;

    public RecipeBookEditorCache() {
        this.categoryID = "";
        this.switchCategories = false;
        this.category = null;
    }

    public void setEditorConfigCopy(RecipeBookConfig original) {
        if (this.editorConfigCopy == null) {
            this.editorConfigCopy = new RecipeBookConfig(original);
        }
    }

    public void resetEditorConfigCopy() {
        this.editorConfigCopy = null;
    }

    public RecipeBookConfig getEditorConfigCopy() {
        return editorConfigCopy;
    }

    public void setCategoriesPage(int categoriesPage) {
        this.categoriesPage = categoriesPage;
    }

    public int getCategoriesPage() {
        return categoriesPage;
    }

    public void setFiltersPage(int filtersPage) {
        this.filtersPage = filtersPage;
    }

    public int getFiltersPage() {
        return filtersPage;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public CategoryFilter getFilter() {
        return categoryFilter;
    }

    public void setFilter(CategoryFilter categoryFilter) {
        this.categoryFilter = categoryFilter;
    }

    public boolean setCategoryID(String categoryID) {
        if (categoryID != null && !categoryID.isEmpty() && VALID_ID.matcher(categoryID).matches()) {
            this.categoryID = categoryID;
            return true;
        } else if (categoryID == null || categoryID.isEmpty()) {
            this.categoryID = null;
        }
        return false;
    }

    public boolean isFilters() {
        return switchCategories;
    }

    public void setFilters(boolean switchCategories) {
        this.switchCategories = switchCategories;
    }

    public CategorySettings getCategorySetting() {
        return isFilters() ? getFilter() : getCategory();
    }

    public boolean hasCategoryID() {
        return categoryID != null && !categoryID.isEmpty();
    }
}
