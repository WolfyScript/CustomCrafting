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

import java.util.Optional;
import me.wolfyscript.customcrafting.data.persistent.CauldronBlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CacheCauldronWorkstation {

    private CauldronBlockData blockData;
    private ItemStack[] input;

    public CacheCauldronWorkstation() {
        this.blockData = null;
        resetInput();
    }

    public Optional<CauldronBlockData> getBlockData() {
        return Optional.ofNullable(blockData);
    }

    public void setBlockData(@Nullable CauldronBlockData blockData) {
        this.blockData = blockData;
    }

    public ItemStack[] getInput() {
        return input;
    }

    public void resetInput() {
        this.input = new ItemStack[6];
    }
}
