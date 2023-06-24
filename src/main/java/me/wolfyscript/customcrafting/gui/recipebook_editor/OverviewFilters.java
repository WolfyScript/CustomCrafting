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

import java.util.List;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OverviewFilters extends Overview {

    public static final String NEXT_PAGE = "next_page";
    public static final String PREVIOUS_PAGE = "previous_page";

    public OverviewFilters(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "filters", customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        getButtonBuilder().action(PREVIOUS_PAGE)
                .state(state -> state.key(ClusterRecipeBookEditor.PREVIOUS_PAGE).icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"))
                        .action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                            cache.getRecipeBookEditorCache().setFiltersPage(Math.max(cache.getRecipeBookEditorCache().getFiltersPage() - 1, 0));
                            return true;
                        })).register();
        getButtonBuilder().action(NEXT_PAGE)
                .state(state -> state.key(ClusterRecipeBookEditor.NEXT_PAGE).icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"))
                        .action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                            cache.getRecipeBookEditorCache().setFiltersPage(Math.min(cache.getRecipeBookEditorCache().getFiltersPage() + 1, 2));
                            return true;
                        })).register();
        getButtonBuilder().action(ADD).state(state-> state.icon(PlayerHeadUtils.getViaURL("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777")).action((cache, guiHandler, player, guiInventory, i, event) -> {
            cache.getRecipeBookEditorCache().setFilter(new CategoryFilter());
            cache.getRecipeBookEditorCache().setCategoryID("");
            guiHandler.openWindow("filter");
            return true;
        })).register();
        for (int i = 0; i < 18; i++) {
            final int iCopy = i;
            getButtonBuilder().action("edit_filter_index_" + i)
                    .state(builder -> builder.key("edit_filter").icon(Material.PAPER)
                            .action((cache, guiHandler, player, guiInventory, slot, event) -> {
                                if (event instanceof InventoryClickEvent clickEvent) {
                                    var recipeBookEditor = cache.getRecipeBookEditorCache();
                                    var recipeBook = recipeBookEditor.getEditorConfigCopy();
                                    int index = recipeBookEditor.getCategoriesPage() + iCopy;
                                    CategoryFilter filter = recipeBook.getFilter(index);
                                    if (clickEvent.isShiftClick()) {
                                        EditorUtils.shiftElement(clickEvent.isLeftClick(), index, recipeBook.getSortedFilters());
                                        return true;
                                    }
                                    if (clickEvent.isLeftClick()) {
                                        recipeBookEditor.setCategoryID(filter.getId());
                                        recipeBookEditor.setFilter(new CategoryFilter(filter));
                                        guiHandler.openWindow("filter");
                                        return true;
                                    }
                                }
                                return true;
                            })
                    )
                    .register();
        }
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        var editorCache = update.getGuiHandler().getCustomCache().getRecipeBookEditorCache();
        var recipeBookConfig = editorCache.getEditorConfigCopy();
        int page = editorCache.getFiltersPage();
        update.setButton(40, ADD);
        update.setButton(38, PREVIOUS_PAGE);
        update.setButton(42, NEXT_PAGE);

        List<String> filters = recipeBookConfig.getSortedFilters();
        for (int i = page * 18, slot = 0; i < filters.size() && slot < 18; i++, slot++) {
            var filter = recipeBookConfig.getFilter(filters.get(i));
            if (filter != null) {
                String id = "filter_" + filter.getId();
                getButtonBuilder()
                        .action(id)
                        .state(state -> state.icon(Material.AIR)
                                .render((cache, guiHandler, player, guiInventory, itemStack, slot2) -> CallbackButtonRender.UpdateResult.of(filter.createItemStack(customCrafting)))
                                .action((cache, guiHandler, player, guiInventory, i1, event) -> true)
                        )
                        .register();
                int finalSlot =  slot + (slot >= 9 ? 9 : 0);
                update.setButton(finalSlot, "filter_" + filter.getId());
                update.setButton(finalSlot + 9, "edit_filter_index_" + slot);
            }
        }
    }
}
