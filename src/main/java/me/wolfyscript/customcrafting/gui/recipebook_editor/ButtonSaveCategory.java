package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.configs.recipebook.CategorySettings;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookEditor;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ButtonSaveCategory extends ActionButton<CCCache> {

    ButtonSaveCategory(boolean saveAs, CustomCrafting customCrafting) {
        super(saveAs ? ClusterRecipeBookEditor.SAVE_AS.getKey() : ClusterRecipeBookEditor.SAVE.getKey(), Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            var recipeBookEditor = cache.getRecipeBookEditor();
            GuiWindow<CCCache> guiWindow = inventory.getWindow();
            WolfyUtilities api = guiHandler.getApi();

            if (saveAs) {
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> {
                    List<String> results = new ArrayList<>();
                    if (args.length == 1) {
                        StringUtil.copyPartialMatches(args[0], customCrafting.getConfigHandler().getRecipeBookConfig().getCategories().getCategories().keySet(), results);
                    }
                    Collections.sort(results);
                    return results;
                });
                guiWindow.openChat(guiHandler.getInvAPI().getGuiCluster(ClusterRecipeBookEditor.KEY), "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    if (s != null && !s.isEmpty() && recipeBookEditor.setCategoryID(s)) {
                        if (saveCategorySetting(recipeBookEditor, customCrafting)) {
                            guiHandler1.openPreviousWindow();
                            return true;
                        }
                        api.getChat().sendKey(player1, ClusterRecipeBookEditor.KEY, "save.error");
                    }
                    return false;
                });
                return true;
            } else if (recipeBookEditor.hasCategoryID()) {
                if (saveCategorySetting(recipeBookEditor, customCrafting)) {
                    guiHandler.openPreviousWindow();
                } else {
                    api.getChat().sendKey(player, ClusterRecipeBookEditor.KEY, "save.error");
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
        if (category instanceof CategoryFilter filter) {
            recipeBook.getCategories().registerFilter(recipeBookEditor.getCategoryID(), filter);
            recipeBookEditor.setFilter(null);
        } else {
            recipeBook.getCategories().registerCategory(recipeBookEditor.getCategoryID(), (Category) category);
            recipeBookEditor.setCategory(null);
        }
        recipeBookEditor.setCategoryID("");
        return true;
    }
}
