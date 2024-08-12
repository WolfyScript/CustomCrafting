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
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

public class MenuTagChooseList extends CCWindow {

    private static final List<Tag<Material>> ITEM_TAGS;

    static {
        ITEM_TAGS = StreamSupport.stream(Bukkit.getTags("items", Material.class).spliterator(), false).sorted(Comparator.comparing(o -> o.getKey().toString())).toList();
    }

    public MenuTagChooseList(GuiCluster<CCCache> guiCluster, CustomCrafting customCrafting) {
        super(guiCluster, "tag_list", 54, customCrafting);
    }

    @Override
    public void onInit() {
        getButtonBuilder().action("next_page").state(s -> s.icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            int page = cache.getRecipeCreatorCache().getTagSettingsCache().getChooseListPage();
            int maxPages = ITEM_TAGS.size() / 45 + ITEM_TAGS.size() % 45 > 0 ? 1 : 0;
            if (page < maxPages) {
                cache.getRecipeCreatorCache().getTagSettingsCache().setChooseListPage(++page);
            }
            return true;
        })).register();
        getButtonBuilder().action("previous_page").state(s -> s.icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            int page = cache.getRecipeCreatorCache().getTagSettingsCache().getChooseListPage();
            if (page > 0) {
                cache.getRecipeCreatorCache().getTagSettingsCache().setChooseListPage(--page);
            }
            return true;
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        var cache = update.getGuiHandler().getCustomCache();
        var tagsCache = cache.getRecipeCreatorCache().getTagSettingsCache();
        int page = tagsCache.getChooseListPage();
        int maxPages = ITEM_TAGS.size() / 45 + (ITEM_TAGS.size() % 45 > 0 ? 1 : 0);
        if (page > maxPages) {
            tagsCache.setChooseListPage(maxPages);
        }

        for (int i = 45 * page, invSlot = 0; i < ITEM_TAGS.size() && invSlot < getSize() - 9; i++, invSlot++) {
            var button = new ButtonTagChoose(ITEM_TAGS.get(i));
            registerButton(button);
            update.setButton(invSlot, button);
        }
        update.setButton(49, ClusterMain.BACK_BOTTOM);

        if (page > 0) {
            update.setButton(48, "previous_page");
        }
        if (page + 1 < maxPages) {
            update.setButton(50, "next_page");
        }
    }
}
