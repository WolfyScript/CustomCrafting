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

package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class StackedContents {

    private final Map<ItemStack, Integer> contents = new HashMap<>();

    public StackedContents() {
    }

    public StackedContents(Inventory inventory) {
        inventory.forEach(this::accountItemStack);
    }

    public void accountItemStack(ItemStack itemStack) {
        accountItemStack(itemStack, 64);
    }

    public void accountItemStack(ItemStack itemStack, int amount) {
        if (!ItemUtils.isAirOrNull(itemStack)) {
            put(itemStack, Math.min(amount, itemStack.getAmount()));
        }
    }

    void put(ItemStack itemStack, int amount) {
        var clonedItem = itemStack.clone();
        clonedItem.setAmount(1);
        int currentAmount = contents.getOrDefault(clonedItem, 0);
        contents.put(clonedItem, currentAmount + amount);
    }

    ItemStack take(ItemStack itemStack, int amount) {
        var clonedItem = itemStack.clone();
        clonedItem.setAmount(1);
        int currentAmount = contents.getOrDefault(clonedItem, 0);
        if (currentAmount >= amount) {
            this.contents.put(clonedItem, currentAmount - amount);
            return itemStack;
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    @Override
    public String toString() {
        return "StackedContents{" +
                "contents=" + contents +
                '}';
    }
}
