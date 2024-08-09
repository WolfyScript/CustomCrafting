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

package me.wolfyscript.customcrafting.gui.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBookConfig;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonType;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

class ButtonCategoryItem extends Button<CCCache> {

    private final CustomCrafting customCrafting;
    private final RecipeBookConfig recipeBookConfig;

    ButtonCategoryItem(CustomCrafting customCrafting) {
        super(ClusterRecipeBook.ITEM_CATEGORY.getKey(), ButtonType.NORMAL);
        this.customCrafting = customCrafting;
        this.recipeBookConfig = customCrafting.getConfigHandler().getRecipeBookConfig();
    }

    @Override
    public boolean execute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int slot, InventoryInteractEvent event) {
        if (event instanceof InventoryClickEvent clickEvent) {
            ButtonContainerRecipeBook.resetButtons(guiHandler);
            if (!recipeBookConfig.getSortedFilters().isEmpty()) {
                RecipeBookCache bookCache = guiHandler.getCustomCache().getRecipeBookCache();
                bookCache.getCategoryFilter().ifPresent(categoryFilter -> {
                    int currentIndex = recipeBookConfig.getSortedFilters().indexOf(categoryFilter.getId());
                    int nextIndex;
                    if (clickEvent.isLeftClick()) {
                        nextIndex = currentIndex < recipeBookConfig.getSortedFilters().size() - 1 ? currentIndex + 1 : 0;
                    } else {
                        nextIndex = currentIndex > 0 ? currentIndex - 1 : recipeBookConfig.getSortedFilters().size() - 1;
                    }
                    bookCache.setCategoryFilter(recipeBookConfig.getFilter(nextIndex));
                });
            }
        }
        return true;
    }

    @Override
    public void render(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        guiHandler.getCustomCache().getRecipeBookCache().getCategoryFilter().ifPresent(filter -> inventory.setItem(slot, filter.createItemStack(customCrafting)));
    }

    @Override
    public void init(GuiWindow guiWindow) {

    }

    @Override
    public void init(GuiCluster<CCCache> guiCluster) {

    }

    @Override
    public void postExecute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {

    }

    @Override
    public void preRender(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, boolean help) {

    }
}
