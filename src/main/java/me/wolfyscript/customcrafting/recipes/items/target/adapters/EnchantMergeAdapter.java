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
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantMergeAdapter extends MergeAdapter {

    private final boolean ignoreEnchantLimit;
    private final boolean ignoreConflicts;
    private final boolean ignoreItemLimit;
    private final boolean increaseLevels;
    private List<Enchantment> blackListedEnchants = new ArrayList<>();

    public EnchantMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "enchant"));
        this.ignoreEnchantLimit = false;
        this.ignoreConflicts = true;
        this.increaseLevels = false;
        this.ignoreItemLimit = true;
    }

    public EnchantMergeAdapter(EnchantMergeAdapter adapter) {
        super(adapter);
        this.ignoreEnchantLimit = adapter.ignoreEnchantLimit;
        this.ignoreConflicts = adapter.ignoreConflicts;
        this.increaseLevels = adapter.increaseLevels;
        this.ignoreItemLimit = adapter.ignoreItemLimit;
        this.blackListedEnchants = List.copyOf(blackListedEnchants);
    }

    public boolean isIgnoreEnchantLimit() {
        return ignoreEnchantLimit;
    }

    @JsonProperty("blackListedEnchants")
    public List<String> getBlackListedEnchants() {
        return blackListedEnchants.stream().map(enchantment -> enchantment.getKey().toString()).toList();
    }

    @JsonProperty("blackListedEnchants")
    public void setBlackListedEnchants(List<String> blackListedEnchants) {
        this.blackListedEnchants = blackListedEnchants.stream().map(s -> Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(s))).toList();
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        Map<Enchantment, Integer> enchants = new HashMap<>();
        for (IngredientData data : recipeData.getBySlots(slots)) {
            data.itemStack().getEnchantments().forEach((enchantment, level) -> {
                if (!blackListedEnchants.contains(enchantment)) {
                    enchants.merge(enchantment, level, (currentLevel, otherLevel) -> increaseLevels && currentLevel.equals(otherLevel) ? ++currentLevel : Math.max(currentLevel, otherLevel));
                }
            });
        }
        var meta = result.getItemMeta();
        enchants.forEach((enchantment, level) -> {
            if ((!result.containsEnchantment(enchantment) || result.getEnchantmentLevel(enchantment) < level) && meta != null) {
                if ((ignoreConflicts || !meta.hasConflictingEnchant(enchantment)) && (ignoreItemLimit || enchantment.canEnchantItem(result))) {
                    meta.addEnchant(enchantment, level, ignoreEnchantLimit);
                }
            }
        });
        result.setItemMeta(meta);
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new EnchantMergeAdapter(this);
    }
}
