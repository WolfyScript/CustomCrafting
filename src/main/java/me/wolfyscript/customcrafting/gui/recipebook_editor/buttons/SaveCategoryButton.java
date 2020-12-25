package me.wolfyscript.customcrafting.gui.recipebook_editor.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBook;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookEditor;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;

public class SaveCategoryButton extends ActionButton<CCCache> {

    public SaveCategoryButton(boolean saveAs, CustomCrafting customCrafting) {
        super(saveAs ? "save_as" : "save", new ButtonState<>("recipe_book_editor", saveAs ? "save_as" : "save", Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            RecipeBookEditor recipeBookEditor = cache.getRecipeBookEditor();
            RecipeBook recipeBook = customCrafting.getConfigHandler().getRecipeBook();
            GuiWindow<CCCache> guiWindow = inventory.getWindow();
            WolfyUtilities api = guiHandler.getApi();

            if (saveAs) {
                guiWindow.openChat(guiHandler.getInvAPI().getGuiCluster("recipe_book_editor"), "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    if (s != null && !s.isEmpty()) {
                        if (recipeBookEditor.setCategoryID(s)) {
                            if (saveRecipe(recipeBookEditor, recipeBook, api, player1)) {
                                guiHandler1.openPreviousWindow();
                                return true;
                            }
                            api.getChat().sendPlayerMessage(player1, "recipe_book_editor", "save.error");
                            return false;
                        }
                    }
                    return false;
                });
                return true;
            } else if (recipeBookEditor.hasCategoryID()) {
                if (saveRecipe(recipeBookEditor, recipeBook, api, player)) {
                    guiHandler.openPreviousWindow();
                } else {
                    api.getChat().sendPlayerMessage(player, "recipe_book_editor", "save.error");
                }
            }
            return true;
        }));
    }

    private static boolean saveRecipe(RecipeBookEditor recipeBookEditor, RecipeBook recipeBook, WolfyUtilities api, Player player) {
        Category category = recipeBookEditor.getCategory();
        if (category.getIcon() == null) {
            return false;
        }
        if (recipeBookEditor.isSwitchCategories()) {
            recipeBook.getCategories().registerSwitchCategory(recipeBookEditor.getCategoryID(), category);
        } else {
            recipeBook.getCategories().registerMainCategory(recipeBookEditor.getCategoryID(), category);
        }
        recipeBookEditor.setCategory(null);
        recipeBookEditor.setCategoryID("");
        try {
            recipeBook.save();
            api.getChat().sendPlayerMessage(player, "recipe_book_editor", "save.success");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
