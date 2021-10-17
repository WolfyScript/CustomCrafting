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

package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EliteWorkbench {

    private byte currentGridSize;
    private EliteWorkbenchData eliteWorkbench;
    private ItemStack result;
    private ItemStack[] contents;

    public EliteWorkbench() {
        this.contents = null;
        this.currentGridSize = 3;
        this.result = new ItemStack(Material.AIR);
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public byte getCurrentGridSize() {
        return currentGridSize;
    }

    public void setCurrentGridSize(byte currentGridSize) {
        this.currentGridSize = currentGridSize;
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public EliteWorkbenchData getEliteWorkbenchData() {
        return eliteWorkbench;
    }

    public void setEliteWorkbenchData(EliteWorkbenchData eliteWorkbench) {
        this.eliteWorkbench = eliteWorkbench;
    }
}
