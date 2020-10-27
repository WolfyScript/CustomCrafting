package me.wolfyscript.customcrafting.gui.recipebook_editor.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBook;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookEditor;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Material;

public class CategoryButton extends ActionButton {

    public CategoryButton(String categoryID, CustomCrafting customCrafting) {
        super("category_" + categoryID, new ButtonState("category", Material.CHEST, (guiHandler, player, inventory, slot, event) -> {
            if (!categoryID.isEmpty()) {
                RecipeBookEditor recipeBookEditor = ((TestCache) guiHandler.getCustomCache()).getRecipeBookEditor();
                RecipeBook recipeBook = customCrafting.getConfigHandler().getRecipeBook();
                if (event.isRightClick() && event.isShiftClick()) {
                    //Delete Category
                    if (recipeBookEditor.isSwitchCategories()) {
                        recipeBook.getCategories().removeSwitchCategory(categoryID);
                    } else {
                        recipeBook.getCategories().removeMainCategory(categoryID);
                    }
                    return true;
                }
                if (event.isLeftClick()) {
                    Category category = recipeBookEditor.isSwitchCategories() ? recipeBook.getCategories().getSwitchCategory(categoryID) : recipeBook.getCategories().getMainCategory(categoryID);
                    //Edit Category
                    recipeBookEditor.setCategoryID(categoryID);
                    recipeBookEditor.setCategory(new Category(category));
                    guiHandler.changeToInv("category");
                    return true;
                }
            }
            return true;
        }, (values, guiHandler, player, itemStack, i, b) -> {
            RecipeBookEditor recipeBookEditor = ((TestCache) guiHandler.getCustomCache()).getRecipeBookEditor();
            RecipeBook recipeBook = customCrafting.getConfigHandler().getRecipeBook();
            Category category = recipeBookEditor.isSwitchCategories() ? recipeBook.getCategories().getSwitchCategory(categoryID) : recipeBook.getCategories().getMainCategory(categoryID);
            itemStack.setType(category.getIcon());
            values.put("%name%", category.getName());
            values.put("%description%", category.getDescription());
            return itemStack;
        }));
    }
}
