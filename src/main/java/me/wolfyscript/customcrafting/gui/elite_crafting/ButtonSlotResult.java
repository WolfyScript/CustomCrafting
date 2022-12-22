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

import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonItemInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.InventoryUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.CacheEliteCraftingTable;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

class ButtonSlotResult {

    static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, CustomCrafting customCrafting) {
        buttonBuilder.itemInput("result_slot").state(state -> state.icon(Material.AIR).action((cache, guiHandler, player, inventory, btn, slot, event) -> {
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
        }).postAction((cache, guiHandler, player, inventory, btn, itemStack, slot, event) -> {
            CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
            if (inventory.getWindow() instanceof CraftingWindow) {
                cacheEliteCraftingTable.setResult(null);
            }
        }).preRender((cache, guiHandler, player, inventory, btn, itemStack, slot, b) -> {
            CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
            ItemStack result = customCrafting.getCraftManager().preCheckRecipe(cacheEliteCraftingTable.getContents(), player, inventory, true, cacheEliteCraftingTable.isAdvancedCraftingRecipes());
            cacheEliteCraftingTable.setResult(result);
        }).render((cache, guiHandler, player, inventory, btn, itemStack, slot) -> {
            CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
            return CallbackButtonRender.UpdateResult.of(cacheEliteCraftingTable.getResult() != null ? cacheEliteCraftingTable.getResult() : new ItemStack(Material.AIR));
        })).register();
    }
}
