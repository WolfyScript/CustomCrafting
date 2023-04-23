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
import java.util.function.Function;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractionUtils {

    /**
     * Used to apply ItemStack changes from the InventoryInteractEvent.
     *
     * @param event          The event from which to get the changed stack.
     * @param applyItemStack The function to apply the stack.
     */
    public static boolean applyItemFromInteractionEvent(int clickedSlot, InventoryInteractEvent event, Set<Integer> draggableSlots, Consumer<ItemStack> applyItemStack) {
        if (event instanceof InventoryClickEvent clickEvent) {
            switch (clickEvent.getAction()) {
                case PLACE_ONE -> applyItemStack.accept(placeStack(clickEvent, stack -> 1));
                case PLACE_SOME ->
                        applyItemStack.accept(placeStack(clickEvent, stack -> Math.min(stack.getMaxStackSize(), stack.getAmount())));
                case PLACE_ALL -> {
                    ItemStack current = clickEvent.getCurrentItem();
                    if (ItemUtils.isAirOrNull(current)) {
                        // Slot is empty so no reference to the inventory stack.
                        // Need to copy the current cursor, because the reference would be cleared.
                        applyItemStack.accept(clickEvent.getCursor().clone());
                    } else {
                        applyItemStack.accept(current);
                    }
                }
                case MOVE_TO_OTHER_INVENTORY -> {
                    ItemStack current = clickEvent.getCurrentItem();
                    if (Objects.equals(clickEvent.getClickedInventory(), clickEvent.getView().getBottomInventory())) {
                        // Cancel the event when trying to shift-click the items from the bottom to the top inventory.
                        return true;
                    }
                    applyItemStack.accept(current);
                }
                // Other than the PICKUP_ALL, DROP_ALL is not called when there is only 1 item.
                // So we need to update the stack manually if that is the case.
                case DROP_ONE_SLOT -> {
                    ItemStack current = clickEvent.getCurrentItem();
                    if (!ItemUtils.isAirOrNull(current) && current.getAmount() == 1) {
                        applyItemStack.accept(null);
                    } else {
                        applyItemStack.accept(current);
                    }
                }
                // Swap means there is both a current and cursor item. The current stack will be swapped with the cursor stack.
                case SWAP_WITH_CURSOR -> applyItemStack.accept(clickEvent.getCursor());
                // These actions cause the slot to be cleared.
                // So reset the stack.
                case PICKUP_ALL, DROP_ALL_SLOT -> applyItemStack.accept(null);
                // This interaction may cause the slot to clear if there is only a single item in the slot, so make sure it is cleared.
                case PICKUP_HALF -> {
                    if (clickEvent.getCurrentItem().getAmount() <= 1) {
                        applyItemStack.accept(null);
                    } else {
                        applyItemStack.accept(clickEvent.getCurrentItem());
                    }
                }
                // All these actions will keep remaining items in the slot, so lets just use the current stack.
                // The stack will be updated, as it is a reference to the stack in the inventory.
                case PICKUP_ONE, PICKUP_SOME, COLLECT_TO_CURSOR, DROP_ALL_CURSOR ->
                        applyItemStack.accept(clickEvent.getCurrentItem());
                // Hotbar swaps work all the same, so lets take the hotbar stack.
                case HOTBAR_SWAP, HOTBAR_MOVE_AND_READD ->
                        applyItemStack.accept(event.getWhoClicked().getInventory().getItem(clickEvent.getHotbarButton()));
                default -> { /* Should not happen inside GUIs. (Like dropping the cursor, cloning it, 'unknown', or 'nothing') */ }
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

    private static ItemStack placeStack(InventoryClickEvent clickEvent, Function<ItemStack, Integer> amount) {
        // If there is already a stack in the slot then just return that, and it'll get updated automatically.
        if (!ItemUtils.isAirOrNull(clickEvent.getCurrentItem())) return clickEvent.getCurrentItem();
        // Current is null, so no reference to the inventory stack.
        ItemStack stack = new ItemStack(Objects.requireNonNull(clickEvent.getCursor()));
        stack.setAmount(amount.apply(stack));
        // This is required to keep sync between the external stack apply method and inventory. Otherwise, the stacks would be different instances!
        // And one tick later, so the event does not need to be cancelled.
        Bukkit.getScheduler().runTask(CustomCrafting.inst(), () -> clickEvent.setCurrentItem(stack));
        return stack;
    }

}
