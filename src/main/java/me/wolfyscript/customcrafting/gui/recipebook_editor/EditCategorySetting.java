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
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

public abstract class EditCategorySetting extends CCWindow {

    protected EditCategorySetting(GuiCluster<CCCache> guiCluster, String namespace, CustomCrafting customCrafting) {
        super(guiCluster, namespace, 54, customCrafting);
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, ClusterRecipeBookEditor.BACK);
        update.setButton(11, ClusterRecipeBookEditor.NAME);
        update.setButton(13, ClusterRecipeBookEditor.ICON);
        update.setButton(15, ClusterRecipeBookEditor.DESCRIPTION_ADD);
        update.setButton(16, ClusterRecipeBookEditor.DESCRIPTION_REMOVE);
        if (update.getGuiHandler().getCustomCache().getRecipeBookEditor().hasCategoryID()) {
            update.setButton(52, ClusterRecipeBookEditor.SAVE);
        }
        update.setButton(53, ClusterRecipeBookEditor.SAVE_AS);
    }
}
