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
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.CacheEliteCraftingTable;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

class ButtonSlotResult extends ItemInputButton<CCCache> {

    ButtonSlotResult(CustomCrafting customCrafting) {
        super("result_slot", new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
            if (event instanceof InventoryClickEvent clickEvent && inventory.getWindow() instanceof CraftingWindow) {
                if (!CraftingWindow.RESULT_SLOTS.contains(slot)) {
                    return true;
                }
                if (clickEvent.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && event.getView().getBottomInventory().equals(clickEvent.getClickedInventory())) {
                    ItemStack itemStack = clickEvent.getCurrentItem() != null ? clickEvent.getCurrentItem().clone() : new ItemStack(Material.AIR);
                    if (!InventoryUtils.hasInventorySpace(cacheEliteCraftingTable.getContents(), itemStack)) {
                        return true;
                    }
                    for (int i = 0; i < cacheEliteCraftingTable.getCurrentGridSize() * cacheEliteCraftingTable.getCurrentGridSize(); i++) {
                        ItemStack item = cacheEliteCraftingTable.getContents()[i];
                        if (item == null) {
                            cacheEliteCraftingTable.getContents()[i] = itemStack;
                            break;
                        } else if ((item.isSimilar(itemStack) || itemStack.isSimilar(item)) && item.getAmount() + itemStack.getAmount() <= itemStack.getMaxStackSize()) {
                            cacheEliteCraftingTable.getContents()[i].setAmount(item.getAmount() + itemStack.getAmount());
                            break;
                        }
                    }
                    return false;
                } else if (!((InventoryClickEvent) event).getClick().equals(ClickType.DOUBLE_CLICK) && !ItemUtils.isAirOrNull(cacheEliteCraftingTable.getResult()) && customCrafting.getCraftManager().has(event.getWhoClicked().getUniqueId())) {
                    if (inventory.getWindow() instanceof CraftingWindow craftingWindow && (ItemUtils.isAirOrNull(clickEvent.getCursor()) || clickEvent.getCursor().isSimilar(cacheEliteCraftingTable.getResult()))) {
                        customCrafting.getCraftManager().get(event.getWhoClicked().getUniqueId()).ifPresent(craftingData -> {
                            customCrafting.getCraftManager().consumeRecipe(clickEvent);
                            cacheEliteCraftingTable.setResult(null);
                            cacheEliteCraftingTable.setContents(new ItemStack[craftingWindow.gridSize * craftingWindow.gridSize]);
                            craftingData.getIndexedBySlot().forEach((integer, ingredientData) -> cacheEliteCraftingTable.getContents()[integer] = ingredientData.itemStack());
                        });
                        customCrafting.getCraftManager().remove(event.getWhoClicked().getUniqueId());
                    }
                }
            }
            return true;
        }, (cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
            if (inventory.getWindow() instanceof CraftingWindow) {
                cacheEliteCraftingTable.setResult(null);
            }
        }, (cache, guiHandler, player, inventory, itemStack, slot, b) -> {
            CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
            EliteWorkbenchData eliteWorkbenchData = cacheEliteCraftingTable.getData();
            ItemStack result = customCrafting.getCraftManager().preCheckRecipe(cacheEliteCraftingTable.getContents(), player, inventory, true, eliteWorkbenchData.isAdvancedRecipes());
            cacheEliteCraftingTable.setResult(result);
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
            return cacheEliteCraftingTable.getResult() != null ? cacheEliteCraftingTable.getResult() : new ItemStack(Material.AIR);
        }));
    }
}
