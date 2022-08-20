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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.wolfyscript.customcrafting.data.persistent.CauldronBlockData;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CacheCauldronWorkstation {

    private Block block;
    private CauldronBlockData blockData;
    private List<ItemStack> input;

    private CauldronPreCookEvent preCookEvent;

    public CacheCauldronWorkstation() {
        this.blockData = null;
        resetInput();
    }

    public Optional<CauldronBlockData> getBlockData() {
        return Optional.ofNullable(blockData);
    }

    public Optional<Block> getBlock() {
        return Optional.ofNullable(block);
    }

    public void setPreCookEvent(CauldronPreCookEvent preCookEvent) {
        this.preCookEvent = preCookEvent;
    }

    public Optional<CauldronPreCookEvent> getPreCookEvent() {
        return Optional.ofNullable(preCookEvent);
    }

    public void setBlock(@Nullable Block block) {
        this.block = block;
    }

    public void setBlockData(@Nullable CauldronBlockData blockData) {
        this.blockData = blockData;
    }

    public List<ItemStack> getInput() {
        return input;
    }

    public void resetInput() {
        this.input = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            input.add(null);
        }
    }
}
