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

import java.util.Objects;

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.context.EvalContextPlayer;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnchantMergeAdapter extends MergeAdapter {

    private final boolean ignoreEnchantLimit;
    private final boolean ignoreConflicts;
    private final boolean ignoreItemLimit;
    private final boolean increaseLevels;
    private List<Enchantment> blackListedEnchants = new ArrayList<>();
    @JsonProperty("addEnchants")
    private Map<Enchantment, Integer> enchantsToAdd = new HashMap<>();
    @JsonProperty("upgradeEnchants")
    private Map<Enchantment, Integer> enchantsToUpgrade = new HashMap<>();
    @JsonProperty("upgradeLevelLimit")
    private Map<Enchantment, Integer> enchantsUpgradeLimit = new HashMap<>();
    @JsonProperty("levelLimit")
    private Map<Enchantment, Integer> enchantsLimit = new HashMap<>();

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
        this.blackListedEnchants = List.copyOf(adapter.blackListedEnchants);
        this.enchantsToAdd = Map.copyOf(adapter.enchantsToAdd);
        this.enchantsToUpgrade = Map.copyOf(adapter.enchantsToUpgrade);
        this.enchantsLimit = Map.copyOf(adapter.enchantsLimit);
        this.enchantsUpgradeLimit = Map.copyOf(adapter.enchantsUpgradeLimit);
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
        this.blackListedEnchants = blackListedEnchants.stream().map(s -> Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(s))).filter(Objects::nonNull).toList();
    }

    @JsonSetter("addEnchants")
    public void setEnchantsToAdd(Map<String, Integer> enchantsToAdd) {
        this.enchantsToAdd = enchantsToAdd.entrySet().stream().collect(Collectors.toMap(entry -> Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(entry.getKey())), Map.Entry::getValue));
    }

    @JsonGetter("addEnchants")
    public Map<String, Integer> getEnchantsToAdd() {
        return enchantsToAdd.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getKey().toString(), Map.Entry::getValue));
    }

    @JsonSetter("upgradeEnchants")
    public void setEnchantsToUpgrade(Map<String, Integer> enchantsToAdd) {
        this.enchantsToUpgrade = enchantsToAdd.entrySet().stream().collect(Collectors.toMap(entry -> Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(entry.getKey())), Map.Entry::getValue));
    }

    @JsonGetter("upgradeEnchants")
    public Map<String, Integer> getEnchantsToUpgrade() {
        return enchantsToUpgrade.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getKey().toString(), Map.Entry::getValue));
    }

    @JsonSetter("upgradeLevelLimit")
    public void setEnchantsUpgradeLimit(Map<String, Integer> enchantsUpgradeLimit) {
        this.enchantsUpgradeLimit = enchantsUpgradeLimit.entrySet().stream().collect(Collectors.toMap(entry -> Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(entry.getKey())), Map.Entry::getValue));
    }

    @JsonSetter("levelLimit")
    public void setEnchantsLimit(Map<String, Integer> enchantsLimit) {
        this.enchantsLimit = enchantsLimit.entrySet().stream().collect(Collectors.toMap(entry -> Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(entry.getKey())), Map.Entry::getValue));
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, StackReference resultReference, ItemStack result) {
        // Merge enchants of the same level and upgrade them
        Map<Enchantment, Integer> enchants = new HashMap<>();
        for (IngredientData data : recipeData.getBySlots(slots)) {
            data.itemStack().getEnchantments().forEach((enchantment, level) -> {
                if (!blackListedEnchants.contains(enchantment)) {
                    mergeEnchant(enchants, enchantment, level);
                }
            });
        }
        if (!enchantsToUpgrade.isEmpty()) {
            //Upgrades existing enchantments by the specified level
            enchantsToUpgrade.forEach((enchantment, lvlUpgrade) -> enchants.computeIfPresent(enchantment, (currentEnchant, level) -> {
                int newLvl = level + lvlUpgrade;
                if (enchantsUpgradeLimit.containsKey(currentEnchant)) {
                    return Math.min(newLvl, enchantsUpgradeLimit.get(currentEnchant));
                }
                return newLvl;
            }));
        }
        if (!enchantsToAdd.isEmpty()) {
            //Adds the configured enchantments to the result, it doesn't exist already.
            enchantsToAdd.forEach(enchants::putIfAbsent);
        }
        //Applying the enchants to the result
        var meta = result.getItemMeta();
        enchants.forEach((enchantment, level) -> {
            if ((!result.containsEnchantment(enchantment) || result.getEnchantmentLevel(enchantment) < level) && meta != null) {
                if ((ignoreConflicts || !meta.hasConflictingEnchant(enchantment)) && (ignoreItemLimit || enchantment.canEnchantItem(result))) {
                    Integer value = enchantsLimit.get(enchantment);
                    if (value != null) {
                        level = Math.min(level, value);
                    }
                    if (ignoreEnchantLimit) {
                        meta.addEnchant(enchantment, level, true);
                    } else {
                        meta.addEnchant(enchantment, Math.min(level, enchantment.getMaxLevel()), false);
                    }
                }
            }
        });
        result.setItemMeta(meta);
        return result;
    }

    protected void mergeEnchant(Map<Enchantment, Integer> enchants, Enchantment enchantment, int level) {
        enchants.merge(enchantment, level, (currentLevel, otherLevel) -> increaseLevels && currentLevel.equals(otherLevel) ? ++currentLevel : Math.max(currentLevel, otherLevel));
    }

    @Override
    public MergeAdapter clone() {
        return new EnchantMergeAdapter(this);
    }
}
