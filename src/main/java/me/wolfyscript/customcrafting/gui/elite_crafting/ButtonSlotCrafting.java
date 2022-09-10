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

package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.CacheEliteCraftingTable;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

class ButtonSlotCrafting extends ItemInputButton<CCCache> {

    ButtonSlotCrafting(int recipeSlot, CustomCrafting customCrafting) {
        super("crafting.slot_" + recipeSlot, new ButtonState<>("", Material.AIR,
                (cache, guiHandler, player, inventory, slot, event) -> cache.getEliteWorkbench() == null || event instanceof InventoryClickEvent clickEvent && CraftingWindow.RESULT_SLOTS.contains(clickEvent.getSlot()),
                (cache, guiHandler, player, inventory, itemStack, slot, b) -> {
                    CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
                    if (cacheEliteCraftingTable.getContents() != null) {
                        cacheEliteCraftingTable.getContents()[recipeSlot] = inventory.getItem(slot);
                        ItemStack result = customCrafting.getCraftManager().preCheckRecipe(cacheEliteCraftingTable.getContents(), player, inventory, true, cacheEliteCraftingTable.getData().isAdvancedRecipes());
                        cacheEliteCraftingTable.setResult(result);
                    } else {
                        cacheEliteCraftingTable.setResult(new ItemStack(Material.AIR));
                    }
                }, null,
                (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
                    CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
                    if (cacheEliteCraftingTable.getContents() != null) {
                        ItemStack slotItem = cacheEliteCraftingTable.getContents()[recipeSlot];
                        return slotItem == null ? new ItemStack(Material.AIR) : slotItem;
                    }
                    return new ItemStack(Material.AIR);
                })
        );
    }
}
