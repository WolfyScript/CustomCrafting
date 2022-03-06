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
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.data.CCCache;
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
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;

class ButtonCategoryItem extends Button<CCCache> {

    private final CustomCrafting customCrafting;
    private final RecipeBookConfig recipeBookConfig;
    private final HashMap<GuiHandler<CCCache>, Integer> categoryMap;

    ButtonCategoryItem(CustomCrafting customCrafting) {
        super(ClusterRecipeBook.ITEM_CATEGORY.getKey(), ButtonType.NORMAL);
        this.customCrafting = customCrafting;
        this.recipeBookConfig = customCrafting.getConfigHandler().getRecipeBookConfig();
        this.categoryMap = new HashMap<>();
    }

    @Override
    public boolean execute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int slot, InventoryInteractEvent event) {
        if (event instanceof InventoryClickEvent clickEvent) {
            ButtonContainerRecipeBook.resetButtons(guiHandler);
            if (!recipeBookConfig.getSortedFilters().isEmpty()) {
                int currentIndex = categoryMap.getOrDefault(guiHandler, 0);
                if (clickEvent.isLeftClick()) {
                    categoryMap.put(guiHandler, currentIndex < recipeBookConfig.getSortedFilters().size() - 1 ? currentIndex + 1 : 0);
                } else {
                    categoryMap.put(guiHandler, currentIndex > 0 ? currentIndex - 1 : recipeBookConfig.getSortedFilters().size() - 1);
                }
            }
        }
        return true;
    }

    @Override
    public void render(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        CategoryFilter category = recipeBookConfig.getFilter(categoryMap.getOrDefault(guiHandler, 0));
        if (category != null) {
            inventory.setItem(slot, category.createItemStack(customCrafting));
        }
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

    @Nullable
    public CategoryFilter getFilter(GuiHandler<CCCache> guiHandler) {
        return recipeBookConfig.getFilter(categoryMap.getOrDefault(guiHandler, 0));
    }
}
