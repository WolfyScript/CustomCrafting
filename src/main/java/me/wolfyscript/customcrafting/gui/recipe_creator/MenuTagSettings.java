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
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class MenuTagSettings extends CCWindow {

    public MenuTagSettings(GuiCluster<CCCache> guiCluster, CustomCrafting customCrafting) {
        super(guiCluster, "tag_settings", 54, customCrafting);
    }

    @Override
    public void onInit() {
        getButtonBuilder().action("add_tag_list").state(s -> s.icon(Material.NAME_TAG).action((cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow("tag_list");
            return true;
        })).register();
        getButtonBuilder().action("next_page").state(s -> s.icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287")).action((cache, guiHandler, player, inventory, slot, event) -> {
            int page = cache.getRecipeCreatorCache().getTagSettingsCache().getListPage();
            cache.getRecipeCreatorCache().getTagSettingsCache().setListPage(++page);
            return true;
        })).register();
        getButtonBuilder().action("previous_page").state(s -> s.icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d")).action((cache, guiHandler, player, inventory, slot, event) -> {
            int page = cache.getRecipeCreatorCache().getTagSettingsCache().getListPage();
            if (page > 0) {
                cache.getRecipeCreatorCache().getTagSettingsCache().setListPage(--page);
            }
            return true;
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        var tagsCache = update.getGuiHandler().getCustomCache().getRecipeCreatorCache().getTagSettingsCache();
        var recipeItemStack = tagsCache.getRecipeItemStack();
        update.setButton(0, ClusterMain.BACK);
        update.setButton(8, ClusterMain.GUI_HELP);
        if (recipeItemStack != null) {
            NamespacedKey[] tags = recipeItemStack.getTags().toArray(new NamespacedKey[0]);
            int page = tagsCache.getListPage();
            int maxPages = tags.length / 45 + (tags.length % 45 > 0 ? 1 : 0);
            if (page > maxPages) {
                tagsCache.setListPage(maxPages);
            }
            if (page > 0) {
                update.setButton(2, "previous_page");
            }
            if (page + 1 < maxPages) {
                update.setButton(4, "next_page");
            }
            for (int i = 45 * page, invSlot = 9; i < tags.length && invSlot < getSize() - 9; i++, invSlot++) {
                var button = new ButtonTagContainer(tags[i]);
                registerButton(button);
                update.setButton(invSlot, button);
            }
        }

        update.setButton(49, "add_tag_list");


    }
}
