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
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

import java.util.List;

public class OverviewCategories extends Overview {

    public OverviewCategories(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "categories", customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        registerButton(new ActionButton<>(ADD, PlayerHeadUtils.getViaURL("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeBookEditor().setCategory(new Category());
            cache.getRecipeBookEditor().setCategoryID("");
            guiHandler.openWindow("category");
            return true;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        var recipeBookConfig = customCrafting.getConfigHandler().getRecipeBookConfig();
        update.setButton(49, ADD);

        List<String> categories = recipeBookConfig.getSortedCategories();
        for (int i = 0; i < categories.size() && i + 9 < 45; i++) {
            var category = recipeBookConfig.getCategory(categories.get(i));
            if (category != null) {
                registerButton(new ButtonCategory(category, customCrafting));
                update.setButton(i + 9, "category_" + category.getId());
            }
        }
    }
}
