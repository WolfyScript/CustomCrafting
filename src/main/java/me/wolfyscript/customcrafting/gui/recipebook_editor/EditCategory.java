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
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

public class EditCategory extends EditCategorySetting {

    private static final String AUTO = "auto";

    public EditCategory(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "category", customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        getButtonBuilder().toggle(AUTO).enabledState(s -> s.subKey("enabled").icon(Material.COMMAND_BLOCK).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeBookEditor().getCategory().setAuto(false);
            return true;
        })).disabledState(s -> s.subKey("disabled").icon(Material.PLAYER_HEAD).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeBookEditor().getCategory().setAuto(true);
            return true;
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        ((ToggleButton<CCCache>) getButton(AUTO)).setState(update.getGuiHandler(), update.getGuiHandler().getCustomCache().getRecipeBookEditor().getCategory().isAuto());

        update.setButton(22, AUTO);
        if (!update.getGuiHandler().getCustomCache().getRecipeBookEditor().getCategory().isAuto()) {
            update.setButton(29, ClusterRecipeBookEditor.RECIPES);
            update.setButton(33, ClusterRecipeBookEditor.FOLDERS);
            update.setButton(40, ClusterRecipeBookEditor.GROUPS);
        }
    }
}
