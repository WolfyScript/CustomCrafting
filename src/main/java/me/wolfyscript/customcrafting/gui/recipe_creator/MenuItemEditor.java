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

package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.item_creator.ClusterItemCreator;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class MenuItemEditor extends CCWindow {

    public MenuItemEditor(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, ClusterRecipeCreator.ITEM_EDITOR.getKey(), 45, customCrafting);
    }

    @Override
    public void onInit() {
        getButtonBuilder().action("back").state(s -> s.key(ClusterMain.BACK).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action((cache, guiHandler, player, inv, i, event) -> {
            if (cache.getSetting().equals(Setting.RECIPE_CREATOR)) {
                cache.getItems().setRecipeItem(false);
                cache.setApplyItem(null);
                guiHandler.openPreviousWindow();
            } else {
                guiHandler.openCluster(ClusterMain.KEY);
            }
            return true;
        })).register();
        getButtonBuilder().action("create_item").state(s -> s.icon(Material.ITEM_FRAME).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.openWindow(ClusterItemCreator.MAIN_MENU);
            return true;
        })).register();
        getButtonBuilder().action(ClusterMain.ITEM_LIST.getKey()).state(s -> s.key(ClusterMain.ITEM_LIST).icon(Material.BOOKSHELF).action((cache, guiHandler, player, inv, i, event) -> {
            guiHandler.openWindow(ClusterMain.ITEM_LIST);
            return true;
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        event.setButton(0, "back");
        event.setButton(8, ClusterMain.GUI_HELP);
        event.setButton(21, ClusterMain.ITEM_LIST.getKey());
        event.setButton(23, "create_item");
    }
}
