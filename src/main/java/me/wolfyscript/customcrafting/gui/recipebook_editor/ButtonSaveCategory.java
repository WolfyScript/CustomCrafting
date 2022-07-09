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
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ButtonSaveCategory extends ActionButton<CCCache> {

    ButtonSaveCategory(boolean saveAs, CustomCrafting customCrafting) {
        super(saveAs ? ClusterRecipeBookEditor.SAVE_AS.getKey() : ClusterRecipeBookEditor.SAVE.getKey(), Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            var recipeBookEditor = cache.getRecipeBookEditor();
            WolfyUtilities api = guiHandler.getApi();

            if (saveAs) {
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> {
                    List<String> results = new ArrayList<>();
                    if (args.length == 1) {
                        StringUtil.copyPartialMatches(args[0], customCrafting.getConfigHandler().getRecipeBookConfig().getCategories().keySet(), results);
                    }
                    Collections.sort(results);
                    return results;
                });
                inventory.getWindow().openChat(guiHandler, inventory.getWindow().getCluster().translatedMsgKey("save.input"), (guiHandler1, player1, s, args) -> {
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
        if (ItemUtils.isAirOrNull(category.getIconStack())) {
            return false;
        }
        if (category instanceof CategoryFilter filter) {
            recipeBook.registerFilter(recipeBookEditor.getCategoryID(), filter);
            recipeBookEditor.setFilter(null);
        } else {
            recipeBook.registerCategory(recipeBookEditor.getCategoryID(), (Category) category);
            recipeBookEditor.setCategory(null);
        }
        recipeBookEditor.setCategoryID("");
        return true;
    }
}
