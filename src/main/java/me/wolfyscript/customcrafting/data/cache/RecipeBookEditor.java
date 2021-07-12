package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.configs.recipebook.CategorySettings;

import java.util.regex.Pattern;

public class RecipeBookEditor {

    private static final Pattern VALID_ID = Pattern.compile("[a-z0-9/._-]+");
    private boolean switchCategories;

    private String categoryID;
    private Category category;
    private CategoryFilter categoryFilter;

    public RecipeBookEditor() {
        this.categoryID = "";
        this.switchCategories = false;
        this.category = null;
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
