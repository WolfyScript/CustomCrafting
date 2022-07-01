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

import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.Nullable;

public class FireworkRocketMergeAdapter extends MergeAdapter {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "firework_rocket");

    private final int powerIncrement;

    public FireworkRocketMergeAdapter() {
        this(1);
    }

    public FireworkRocketMergeAdapter(int powerIncrement) {
        super(KEY);
        this.powerIncrement = powerIncrement;
    }

    public FireworkRocketMergeAdapter(FireworkRocketMergeAdapter rocketAdapter) {
        super(KEY);
        this.powerIncrement = rocketAdapter.powerIncrement;
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        if (result.getType().equals(Material.FIREWORK_ROCKET)) {
            if (result.getItemMeta() instanceof FireworkMeta meta) {
                for (IngredientData bySlot : recipeData.getBySlots(slots)) {
                    var item = bySlot.itemStack();
                    if (item.getType().equals(Material.GUNPOWDER)) {
                        meta.setPower(meta.getPower() + powerIncrement);
                    } else if (item.getItemMeta() instanceof FireworkEffectMeta effectMeta) {
                        if (effectMeta.hasEffect()) {
                            meta.addEffect(effectMeta.getEffect());
                        }
                    }
                }
                result.setItemMeta(meta);
            }
        }
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new FireworkRocketMergeAdapter(this);
    }
}
