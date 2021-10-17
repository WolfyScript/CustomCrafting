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
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
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
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            if (event instanceof InventoryClickEvent clickEvent && inventory.getWindow() instanceof CraftingWindow) {
                if (clickEvent.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && event.getView().getBottomInventory().equals(clickEvent.getClickedInventory())) {
                    ItemStack itemStack = clickEvent.getCurrentItem().clone();
                    if (!InventoryUtils.hasInventorySpace(eliteWorkbench.getContents(), itemStack)) {
                        return true;
                    }
                    for (int i = 0; i < eliteWorkbench.getCurrentGridSize() * eliteWorkbench.getCurrentGridSize(); i++) {
                        ItemStack item = eliteWorkbench.getContents()[i];
                        if (item == null) {
                            eliteWorkbench.getContents()[i] = itemStack;
                            break;
                        } else if ((item.isSimilar(itemStack) || itemStack.isSimilar(item)) && item.getAmount() + itemStack.getAmount() <= itemStack.getMaxStackSize()) {
                            eliteWorkbench.getContents()[i].setAmount(item.getAmount() + itemStack.getAmount());
                            break;
                        }
                    }
                    return false;
                } else if (!((InventoryClickEvent) event).getClick().equals(ClickType.DOUBLE_CLICK) && eliteWorkbench.getResult() != null && customCrafting.getCraftManager().has(event.getWhoClicked().getUniqueId())) {
                    if (ItemUtils.isAirOrNull(clickEvent.getCursor()) || clickEvent.getCursor().isSimilar(eliteWorkbench.getResult())) {
                        customCrafting.getCraftManager().consumeRecipe(eliteWorkbench.getResult(), clickEvent);
                        eliteWorkbench.setResult(null);
                        customCrafting.getCraftManager().remove(event.getWhoClicked().getUniqueId());
                    }
                }
            }
            return true;
        }, (cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            if (inventory.getWindow() instanceof CraftingWindow) {
                eliteWorkbench.setResult(null);
            }
        }, (cache, guiHandler, player, inventory, itemStack, slot, b) -> {
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            EliteWorkbenchData eliteWorkbenchData = eliteWorkbench.getEliteWorkbenchData();
            ItemStack result = customCrafting.getCraftManager().preCheckRecipe(eliteWorkbench.getContents(), player, inventory, true, eliteWorkbenchData.isAdvancedRecipes());
            eliteWorkbench.setResult(result);
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            return eliteWorkbench.getResult() != null ? eliteWorkbench.getResult() : new ItemStack(Material.AIR);
        }));
    }
}
