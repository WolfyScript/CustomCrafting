package me.wolfyscript.customcrafting.gui.recipebook_editor.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBook;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookEditor;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SaveCategoryButton extends ActionButton {

    private final CustomCrafting customCrafting;

    public SaveCategoryButton(boolean saveAs, CustomCrafting customCrafting) {
        super(saveAs ? "save_as" : "save", new ButtonState("recipe_book_editor", saveAs ? "save_as" : "save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            RecipeBookEditor recipeBookEditor = cache.getRecipeBookEditor();
            RecipeBook recipeBook = customCrafting.getConfigHandler().getRecipeBook();
            GuiWindow guiWindow = guiHandler.getCurrentInv();
            WolfyUtilities api = guiHandler.getCurrentInv().getAPI();

            if (saveAs) {
                guiWindow.openChat("recipe_book_editor", "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    if (s != null && !s.isEmpty()) {
                        if (recipeBookEditor.setCategoryID(s)) {
                            if (saveRecipe(recipeBookEditor, recipeBook, api, player1)) {
                                guiHandler1.openPreviousInv();
                                return true;
                            }
                            api.sendPlayerMessage(player1, "recipe_book_editor", "save.error");
                            return false;
                        }
                    }
                    return false;
                });
                return true;
            } else if (recipeBookEditor.hasCategoryID()) {
                if (saveRecipe(recipeBookEditor, recipeBook, api, player)) {
                    guiHandler.openPreviousInv();
                } else {
                    api.sendPlayerMessage(player, "recipe_book_editor", "save.error");
                }
            }
            return true;
        }));
        this.customCrafting = customCrafting;
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
        api.sendPlayerMessage(player, "recipe_book_editor", "save.success");
        return true;
    }
}
