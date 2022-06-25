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

package me.wolfyscript.customcrafting.recipes.items.target.adapters;

import com.wolfyscript.lib.nbt.nbtapi.NBTCompound;
import com.wolfyscript.lib.nbt.nbtapi.NBTItem;
import com.wolfyscript.utilities.bukkit.nbt.NBTQuery;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NBTMergeAdapter extends MergeAdapter {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "nbt/merge");

    private NBTQuery query = null;

    public NBTMergeAdapter() {
        super(KEY);
    }

    protected NBTMergeAdapter(NBTMergeAdapter adapter) {
        super(adapter);
        this.query = adapter.query.copy();
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        NBTItem nbtResultStack = new NBTItem(result);
        for (IngredientData slotData : recipeData.getBySlots(slots)) {
            var itemStack = slotData.itemStack();
            NBTItem nbtStack = new NBTItem(itemStack);
            NBTCompound resultCompound = query.run(nbtStack);
            nbtResultStack.mergeCompound(resultCompound);
        }
        nbtResultStack.applyNBT(result);
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new NBTMergeAdapter(this);
    }
}
