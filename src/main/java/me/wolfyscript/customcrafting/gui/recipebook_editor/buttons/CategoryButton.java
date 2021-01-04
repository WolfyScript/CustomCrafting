package me.wolfyscript.customcrafting.gui.recipebook_editor.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBook;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookEditor;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CategoryButton extends ActionButton<CCCache> {

    public CategoryButton(String categoryID, CustomCrafting customCrafting) {
        super("category_" + categoryID, new ButtonState<>("category", Material.CHEST, (cache, guiHandler, player, inventory, slot, event) -> {
            if (!categoryID.isEmpty()) {
                RecipeBookEditor recipeBookEditor = guiHandler.getCustomCache().getRecipeBookEditor();
                RecipeBook recipeBook = customCrafting.getConfigHandler().getRecipeBook();
                if(event instanceof InventoryClickEvent){
                    if (((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                        //Delete Category
                        if (recipeBookEditor.isSwitchCategories()) {
                            recipeBook.getCategories().removeSwitchCategory(categoryID);
                        } else {
                            recipeBook.getCategories().removeMainCategory(categoryID);
                        }
                        return true;
                    }
                    if (((InventoryClickEvent) event).isLeftClick()) {
                        //Edit Category
                        Category category = recipeBookEditor.isSwitchCategories() ? recipeBook.getCategories().getSwitchCategory(categoryID) : recipeBook.getCategories().getMainCategory(categoryID);
                        recipeBookEditor.setCategoryID(categoryID);
                        recipeBookEditor.setCategory(new Category(category));
                        guiHandler.openWindow("category");
                        return true;
                    }
                }
            }
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            RecipeBookEditor recipeBookEditor = guiHandler.getCustomCache().getRecipeBookEditor();
            RecipeBook recipeBook = customCrafting.getConfigHandler().getRecipeBook();
            Category category = recipeBookEditor.isSwitchCategories() ? recipeBook.getCategories().getSwitchCategory(categoryID) : recipeBook.getCategories().getMainCategory(categoryID);
            itemStack.setType(category.getIcon());
            values.put("%name%", category.getName());
            values.put("%description%", category.getDescription());
            return itemStack;
        }));
    }
}
