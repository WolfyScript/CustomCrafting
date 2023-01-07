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
    public static boolean applyItemFromInteractionEvent(InventoryInteractEvent event, Consumer<ItemStack> applyItemStack) {
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
                case PICKUP_ONE, PICKUP_HALF, PICKUP_SOME, COLLECT_TO_CURSOR -> applyItemStack.accept(current);
                case PICKUP_ALL -> applyItemStack.accept(null);
            }
            return false;
        } else if (event instanceof InventoryDragEvent dragEvent) {
            dragEvent.setCancelled(true); // Do not handle drag events! Too complicated (For now... TODO: (v5) might include it).
            return true;
        }
        return true;
    }

}
