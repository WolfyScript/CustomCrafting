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

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.chat.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DisplayNameMergeAdapter extends MergeAdapter {

    private boolean appendNames;
    private String extra;

    public DisplayNameMergeAdapter() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, "display_name"));
        this.appendNames = false;
        this.extra = null;
    }

    public DisplayNameMergeAdapter(DisplayNameMergeAdapter adapter) {
        super(adapter);
        this.appendNames = adapter.appendNames;
        this.extra = adapter.extra;
    }

    public void setAppendNames(boolean appendNames) {
        this.appendNames = appendNames;
    }

    public boolean isAppendNames() {
        return appendNames;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtra() {
        return extra;
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        var resultMeta = result.getItemMeta();
        for (IngredientData data : recipeData.getBySlots(slots)) {
            var item = data.itemStack();
            var meta = item.getItemMeta();
            if (appendNames) {
                resultMeta.setDisplayName(meta.getDisplayName() + resultMeta.getDisplayName());
            } else {
                resultMeta.setDisplayName(meta.getDisplayName());
            }
        }
        if (extra != null) {
            resultMeta.setDisplayName(resultMeta.getDisplayName() + ChatColor.convert(extra));
        }
        result.setItemMeta(resultMeta);
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new DisplayNameMergeAdapter(this);
    }
}
