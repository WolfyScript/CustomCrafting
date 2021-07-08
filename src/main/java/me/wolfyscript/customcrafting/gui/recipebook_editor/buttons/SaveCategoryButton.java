package me.wolfyscript.customcrafting.gui.recipebook_editor.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.configs.recipebook.CategorySettings;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookEditor;
import me.wolfyscript.customcrafting.gui.RecipeBookEditorCluster;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;

public class SaveCategoryButton extends ActionButton<CCCache> {

    public SaveCategoryButton(boolean saveAs, CustomCrafting customCrafting) {
        super(saveAs ? RecipeBookEditorCluster.SAVE_AS.getKey() : RecipeBookEditorCluster.SAVE.getKey(), Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            var recipeBookEditor = cache.getRecipeBookEditor();
            GuiWindow<CCCache> guiWindow = inventory.getWindow();
            WolfyUtilities api = guiHandler.getApi();

            if (saveAs) {
                guiWindow.openChat(guiHandler.getInvAPI().getGuiCluster("recipe_book_editor"), "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    if (s != null && !s.isEmpty() && recipeBookEditor.setCategoryID(s)) {
                        if (saveCategorySetting(recipeBookEditor, customCrafting)) {
                            guiHandler1.openPreviousWindow();
                            return true;
                        }
                        api.getChat().sendKey(player1, "recipe_book_editor", "save.error");
                    }
                    return false;
                });
                return true;
            } else if (recipeBookEditor.hasCategoryID()) {
                if (saveCategorySetting(recipeBookEditor, customCrafting)) {
                    guiHandler.openPreviousWindow();
                } else {
                    api.getChat().sendKey(player, "recipe_book_editor", "save.error");
                }
            }
            return true;
        });
    }

    private static boolean saveCategorySetting(RecipeBookEditor recipeBookEditor, CustomCrafting customCrafting) {
        var recipeBook = customCrafting.getConfigHandler().getRecipeBookConfig();
        CategorySettings category = recipeBookEditor.getCategorySetting();
        if (category.getIcon() == null) {
            return false;
        }
        if (category instanceof CategoryFilter) {
            recipeBook.getCategories().registerFilter(recipeBookEditor.getCategoryID(), (CategoryFilter) category);
            recipeBookEditor.setFilter(null);
        } else {
            recipeBook.getCategories().registerCategory(recipeBookEditor.getCategoryID(), (Category) category);
            recipeBookEditor.setCategory(null);
        }
        recipeBookEditor.setCategoryID("");
        return true;
    }
}
