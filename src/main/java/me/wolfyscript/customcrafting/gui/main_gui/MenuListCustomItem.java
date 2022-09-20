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

package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.recipe_creator.ClusterRecipeCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MenuListCustomItem extends CCWindow {

    public MenuListCustomItem(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "item_list", 54, customCrafting);
    }

    @Override
    public void onInit() {
        getButtonBuilder().action("back").state(s -> s.key(ClusterMain.BACK).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (items.getListNamespace() != null) {
                items.setListNamespace(null);
            } else if (cache.getSetting().equals(Setting.RECIPE_CREATOR)) {
                List<? extends GuiWindow<?>> history = guiHandler.getClusterHistory().get(guiHandler.getCluster());
                history.remove(history.size() - 1);
                guiHandler.openCluster(ClusterRecipeCreator.KEY);
            } else {
                guiHandler.openPreviousWindow();
            }
            return true;
        })).register();
        getButtonBuilder().action("next_page").state(s -> s.icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.setListPage(cache.getItems().getListPage() + 1);
            return true;
        })).register();
        getButtonBuilder().action("previous_page").state(s -> s.icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            int page = cache.getItems().getListPage();
            if (page > 0) {
                items.setListPage(--page);
            }
            return true;
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        update.setButton(8, ClusterMain.GUI_HELP);
        var items = update.getGuiHandler().getCustomCache().getItems();
        int maxPages;
        int page;
        String namespace = items.getListNamespace();
        var registry = api.getRegistries().getCustomItems();
        if (namespace == null) {
            List<String> namespaceList = registry.keySet().parallelStream().filter(key -> key.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)).map(NamespacedKeyUtils::getInternalNamespace).distinct().filter(Objects::nonNull).sorted(String::compareToIgnoreCase).toList();
            maxPages = namespaceList.size() / 45 + (namespaceList.size() % 45 > 0 ? 1 : 0);
            page = items.getListPage(maxPages);
            for (int i = 45 * page, item = 9; item < 54 && i < namespaceList.size(); i++, item++) {
                String btnID = "namespace_" + namespaceList.get(i);
                if (!hasButton("namespace_" + namespaceList.get(i))) {
                    registerButton(new ButtonNamespaceItem(namespaceList.get(i)));
                }
                update.setButton(item, btnID);
            }
        } else {
            List<CustomItem> customItems = registry.entrySet().parallelStream().filter(entry -> entry.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE) && namespace.equals(NamespacedKeyUtils.getInternalNamespace(entry.getKey()))).map(Map.Entry::getValue).toList();
            maxPages = customItems.size() / 45 + (customItems.size() % 45 > 0 ? 1 : 0);
            page = items.getListPage(maxPages);

            for (int i = items.getListPage() * 45, s = 9; i < customItems.size() && s < 54; i++, s++) {
                var namespacedKey = customItems.get(i).getNamespacedKey();
                String id = "item_" + namespacedKey.toString("__");
                if (!hasButton(id)) {
                    registerButton(new ButtonSelectCustomItem(customCrafting, namespacedKey));
                }
                update.setButton(s, id);
            }
        }
        if (page > 0) {
            update.setButton(3, "previous_page");
        }
        if (page + 1 < maxPages) {
            update.setButton(6, "next_page");
        }
    }
}
