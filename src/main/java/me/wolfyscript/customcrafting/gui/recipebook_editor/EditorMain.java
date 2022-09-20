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
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;

public class EditorMain extends CCWindow {

    private static final String SAVE = "save";
    private static final String CANCEL = "cancel";
    private static final String FILTERS = "filters";
    private static final String CATEGORIES = "categories";

    public EditorMain(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "editor_main", 54, customCrafting);
    }

    @Override
    public void onInit() {
        getButtonBuilder().action(CANCEL).state(s -> s.icon(Material.BARRIER).action((cache, guiHandler, player, inventory, slot, event) -> {
            customCrafting.getConfigHandler().loadRecipeBookConfig();
            guiHandler.openCluster("none");
            return true;
        })).register();
        getButtonBuilder().action(SAVE).state(s -> s.icon(Material.WRITTEN_BOOK).action((cache, guiHandler, player, inventory, slot, event) -> {
            try {
                if (!new File(customCrafting.getDataFolder(), "recipe_book.conf").renameTo(new File(customCrafting.getDataFolder(), "recipe_book_backup.conf"))) {
                    sendMessage(guiHandler, getCluster().translatedMsgKey("save.failed_backup"));
                }
                customCrafting.getConfigHandler().save();
                sendMessage(guiHandler, getCluster().translatedMsgKey("save.success"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            guiHandler.openCluster("none");
            return true;
        })).register();
        getButtonBuilder().action(FILTERS).state(s -> s.icon(Material.COMPASS).action((cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().setFilters(true);
            guiHandler.openWindow(FILTERS);
            return true;
        })).register();
        getButtonBuilder().action(CATEGORIES).state(s -> s.icon(Material.CHEST).action((cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().setFilters(false);
            guiHandler.openWindow(CATEGORIES);
            return true;
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, customCrafting.getConfigHandler().getConfig().isGUIDrawBackground() ? PlayerUtil.getStore(update.getPlayer()).getLightBackground() : ClusterMain.EMPTY);
        update.setButton(20, CATEGORIES);
        update.setButton(24, FILTERS);

        update.setButton(39, CANCEL);
        update.setButton(41, SAVE);
    }
}
