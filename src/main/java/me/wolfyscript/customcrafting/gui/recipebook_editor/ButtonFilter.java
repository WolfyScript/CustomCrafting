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
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

class ButtonFilter extends ActionButton<CCCache> {

    ButtonFilter(CategoryFilter filter, CustomCrafting customCrafting) {
        super("filter_" + filter.getId(), new ButtonState<>("filter", Material.CHEST, (cache, guiHandler, player, inventory, slot, event) -> {
            if (event instanceof InventoryClickEvent clickEvent) {
                var recipeBookEditor = guiHandler.getCustomCache().getRecipeBookEditor();
                var recipeBook = customCrafting.getConfigHandler().getRecipeBookConfig();
                if (clickEvent.isRightClick() && clickEvent.isShiftClick()) {
                    //Delete Filter
                    recipeBook.removeFilter(filter.getId());
                    return true;
                } else if (clickEvent.isLeftClick()) {
                    //Edit Category
                    recipeBookEditor.setCategoryID(filter.getId());
                    recipeBookEditor.setFilter(new CategoryFilter(filter));
                    guiHandler.openWindow("filter");
                    return true;
                }
            }
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            itemStack = filter.getIconStack();
            values.put("%name%", filter.getName());
            values.put("%description%", filter.getDescription());
            return itemStack;
        }));
    }
}
