package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.configs.recipebook.CategorySettings;

import java.util.regex.Pattern;

public class RecipeBookEditor {

    private static final Pattern VALID_ID = Pattern.compile("[a-z0-9/._-]+");
    private boolean switchCategories;

    private String categoryID;
    private CategorySettings category;

    public RecipeBookEditor() {
        this.categoryID = "";
        this.switchCategories = false;
        this.category = null;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public CategorySettings getCategory() {
        return category;
    }

    public void setCategory(CategorySettings category) {
        this.category = category;
    }

    public boolean setCategoryID(String categoryID) {
        if (categoryID != null && !categoryID.isEmpty() && VALID_ID.matcher(categoryID).matches()) {
            this.categoryID = categoryID;
            return true;
        }
        return false;
    }

    public boolean isSwitchCategories() {
        return switchCategories;
    }

    public void setSwitchCategories(boolean switchCategories) {
        this.switchCategories = switchCategories;
    }

    public boolean hasCategoryID() {
        return categoryID != null && !categoryID.isEmpty();
    }
}
