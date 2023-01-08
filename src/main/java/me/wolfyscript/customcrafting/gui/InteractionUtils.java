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

package me.wolfyscript.customcrafting.gui;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractionUtils {

    /**
     * Used to apply ItemStack changes from the InventoryInteractEvent.
     *
     * @param event The event from which to get the changed stack.
     * @param applyItemStack The function to apply the stack.
     */
    public static boolean applyItemFromInteractionEvent(int clickedSlot, InventoryInteractEvent event, Set<Integer> draggableSlots, Consumer<ItemStack> applyItemStack) {
        if (event instanceof InventoryClickEvent clickEvent) {
            ItemStack cursor = clickEvent.getCursor();
            ItemStack current = clickEvent.getCurrentItem();
            switch (clickEvent.getAction()) {
                case PLACE_ONE -> {
                    ItemStack stack;
                    if (ItemUtils.isAirOrNull(current)) {
                        stack = cursor.clone();
                        stack.setAmount(1);
                        clickEvent.setCurrentItem(stack);
                    } else {
                        stack = current;
                    }
                    applyItemStack.accept(stack);
                }
                case PLACE_SOME -> {
                    ItemStack stack;
                    if (ItemUtils.isAirOrNull(current)) {
                        stack = cursor.clone();
                        stack.setAmount(Math.min(stack.getMaxStackSize(), cursor.getAmount()));
                        clickEvent.setCurrentItem(stack);
                    } else {
                        stack = current;
                    }
                    applyItemStack.accept(stack);
                }
                case PLACE_ALL -> {
                    if (ItemUtils.isAirOrNull(current)) {
                        applyItemStack.accept(cursor.clone());
                    } else {
                        applyItemStack.accept(current);
                    }
                }
                case MOVE_TO_OTHER_INVENTORY -> {
                    if (Objects.equals(clickEvent.getClickedInventory(), clickEvent.getView().getBottomInventory())) {
                        // Cancel the event when trying to shift-click the items from the bottom to the top inventory.
                        return true;
                    }
                    applyItemStack.accept(current);
                }
                case DROP_ONE_SLOT -> {
                    // Other than the PICKUP_ALL, DROP_ALL is not called when there is only 1 item.
                    // So we need to update the stack manually if that is the case.
                    if (!ItemUtils.isAirOrNull(current) && current.getAmount() == 1) {
                        applyItemStack.accept(null);
                    } else {
                        applyItemStack.accept(current);
                    }
                }
                // Swap means there is both a current and cursor item. The current stack will be swapped with the cursor stack.
                case SWAP_WITH_CURSOR -> applyItemStack.accept(cursor);
                // These actions cause the slot to be cleared.
                // So reset the stack.
                case PICKUP_ALL, DROP_ALL_SLOT -> applyItemStack.accept(null);
                // All these actions will keep remaining items in the slot, so lets just use the current stack.
                // The stack will be updated, as it is a reference to the stack in the inventory.
                case PICKUP_ONE, PICKUP_HALF, PICKUP_SOME, COLLECT_TO_CURSOR, DROP_ALL_CURSOR -> applyItemStack.accept(current);
                // Hotbar swaps work all the same, so lets take the hotbar stack.
                case HOTBAR_SWAP, HOTBAR_MOVE_AND_READD -> applyItemStack.accept(event.getWhoClicked().getInventory().getItem(clickEvent.getHotbarButton()));
                default -> { /* Should not happen */ }
            }
            return false;
        } else if (event instanceof InventoryDragEvent dragEvent) {
            if (draggableSlots.containsAll(dragEvent.getInventorySlots())) {
                applyItemStack.accept(dragEvent.getNewItems().get(clickedSlot));
                return false;
            } else {
                // Do not handle drag events that span across other gui slots.
                dragEvent.setCancelled(true);
            }
        }
        return true;
    }

}
