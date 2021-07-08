package me.wolfyscript.customcrafting.gui.recipebook_editor.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.configs.recipebook.CategorySettings;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CategoryButton extends ActionButton<CCCache> {

    public CategoryButton(String categoryID, CustomCrafting customCrafting) {
        super("category_" + categoryID, new ButtonState<>("category", Material.CHEST, (cache, guiHandler, player, inventory, slot, event) -> {
            if (!categoryID.isEmpty()) {
                var recipeBookEditor = guiHandler.getCustomCache().getRecipeBookEditor();
                var recipeBook = customCrafting.getConfigHandler().getRecipeBookConfig();
                if (event instanceof InventoryClickEvent) {
                    if (((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                        //Delete Category
                        if (recipeBookEditor.isFilters()) {
                            recipeBook.getCategories().removeFilter(categoryID);
                        } else {
                            recipeBook.getCategories().removeCategory(categoryID);
                        }
                        return true;
                    }
                    if (((InventoryClickEvent) event).isLeftClick()) {
                        //Edit Category
                        CategorySettings category = recipeBookEditor.isFilters() ? recipeBook.getCategories().getFilter(categoryID) : recipeBook.getCategories().getCategory(categoryID);
                        recipeBookEditor.setCategoryID(categoryID);
                        if (category instanceof CategoryFilter) {
                            recipeBookEditor.setFilter(new CategoryFilter((CategoryFilter) category));
                            guiHandler.openWindow("filter");
                        } else {
                            recipeBookEditor.setCategory(new Category((Category) category));
                            guiHandler.openWindow("category");
                        }
                        return true;
                    }
                }
            }
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var recipeBookEditor = guiHandler.getCustomCache().getRecipeBookEditor();
            var recipeBook = customCrafting.getConfigHandler().getRecipeBookConfig();
            CategorySettings category = recipeBookEditor.isFilters() ? recipeBook.getCategories().getFilter(categoryID) : recipeBook.getCategories().getCategory(categoryID);
            itemStack.setType(category.getIcon());
            values.put("%name%", category.getName());
            values.put("%description%", category.getDescription());
            return itemStack;
        }));
    }
}
