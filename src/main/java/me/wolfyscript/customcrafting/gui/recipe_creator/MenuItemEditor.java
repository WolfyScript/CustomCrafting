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

import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.item_creator.ClusterItemCreator;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import org.bukkit.Material;

public class MenuItemEditor extends CCWindow {

    public MenuItemEditor(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, ClusterRecipeCreator.ITEM_EDITOR.getKey(), 45, customCrafting);
    }

    @Override
    public void onInit() {
        getButtonBuilder().action("back").state(s -> s.key(ClusterMain.BACK).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action((holder, cache, btn, slot, details) -> {
            if (cache.getSetting().equals(Setting.RECIPE_CREATOR)) {
                cache.getItems().setRecipeItem(false);
                cache.setApplyItem(null);
                holder.getGuiHandler().openPreviousWindow();
            } else {
                holder.getGuiHandler().openCluster(ClusterMain.KEY);
            }
            return ButtonInteractionResult.cancel(true);
        })).register();
        getButtonBuilder().action("create_item").state(s -> s.icon(Material.ITEM_FRAME).action((holder, cache, btn, slot, details) -> {
            holder.getGuiHandler().openWindow(ClusterItemCreator.MAIN_MENU);
            return ButtonInteractionResult.cancel(true);
        })).register();
        getButtonBuilder().action(ClusterMain.ITEM_LIST.getKey()).state(s -> s.key(ClusterMain.ITEM_LIST).icon(Material.BOOKSHELF).action((holder, cache, btn, slot, details) -> {
            holder.getGuiHandler().openWindow(ClusterMain.ITEM_LIST);
            return ButtonInteractionResult.cancel(true);
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
