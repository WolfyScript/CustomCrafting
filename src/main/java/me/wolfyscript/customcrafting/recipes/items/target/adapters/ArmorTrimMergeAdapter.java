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
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ArmorTrimMergeAdapter extends MergeAdapter {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "armor_trim");

    // Json Values
    private boolean copyPattern;
    private boolean copyMaterial;

    private int[] copyPatternsFrom;
    private int[] copyMaterialsFrom;

    private TrimPattern defaultPattern;
    private TrimMaterial defaultMaterial;

    public ArmorTrimMergeAdapter() {
        super(KEY);
        this.copyPattern = true;
        this.copyMaterial = true;
    }

    public ArmorTrimMergeAdapter(ArmorTrimMergeAdapter adapter) {
        super(adapter);
        this.copyPattern = adapter.copyPattern;
        this.copyMaterial = adapter.copyMaterial;
        this.copyMaterialsFrom = Arrays.copyOf(adapter.copyMaterialsFrom, adapter.copyMaterialsFrom.length);
        this.copyPatternsFrom = Arrays.copyOf(adapter.copyPatternsFrom, adapter.copyPatternsFrom.length);
        this.defaultPattern = adapter.defaultPattern;
        this.defaultMaterial = adapter.defaultMaterial;
    }

    @JsonIgnore
    public void setDefaultMaterial(TrimMaterial defaultMaterial) {
        this.defaultMaterial = defaultMaterial;
    }

    @JsonIgnore
    public TrimMaterial getDefaultMaterial() {
        return defaultMaterial;
    }

    @JsonIgnore
    public void setDefaultPattern(TrimPattern defaultPattern) {
        this.defaultPattern = defaultPattern;
    }

    @JsonIgnore
    public TrimPattern getDefaultPattern() {
        return defaultPattern;
    }

    @JsonIgnore
    public void setCopyMaterial(boolean copyMaterial) {
        this.copyMaterial = copyMaterial;
    }

    @JsonIgnore
    public void setCopyMaterialsFrom(int[] copyMaterialsFrom) {
        this.copyMaterialsFrom = copyMaterialsFrom;
    }

    @JsonIgnore
    public void setCopyPattern(boolean copyPattern) {
        this.copyPattern = copyPattern;
    }

    @JsonIgnore
    public void setCopyPatternsFrom(int[] copyPatternsFrom) {
        this.copyPatternsFrom = copyPatternsFrom;
    }

    @JsonSetter("defaultMaterial")
    private void setJsonDefaultMaterial(String defaultMaterial) {
        org.bukkit.NamespacedKey key = org.bukkit.NamespacedKey.fromString(defaultMaterial);
        if (key == null) return;
        this.defaultMaterial = Registry.TRIM_MATERIAL.get(key);
    }

    @JsonGetter("defaultMaterial")
    private String getJsonDefaultMaterial() {
        return defaultMaterial == null ? null : defaultMaterial.getKey().toString();
    }

    @JsonSetter("defaultPattern")
    private void setJsonDefaultPattern(String defaultPattern) {
        org.bukkit.NamespacedKey key = org.bukkit.NamespacedKey.fromString(defaultPattern);
        if (key == null) return;
        this.defaultPattern = Registry.TRIM_PATTERN.get(key);
    }

    @JsonGetter("defaultPattern")
    private String getJsonDefaultPattern() {
        return defaultPattern == null ? null : defaultPattern.getKey().toString();
    }

    @JsonSetter("copyPattern")
    private void setCopyPattern(JsonNode node) {
        if (node.isArray()) {
            copyPattern = false;
            copyPatternsFrom = new int[node.size()];
            int i = 0;
            for (JsonNode jsonNode : node) {
                copyPatternsFrom[i++] = jsonNode.asInt();
            }
            return;
        }
        copyPattern = node.asBoolean();
    }

    @JsonGetter("copyPattern")
    private Object getCopyPattern() {
        if (copyPattern) {
            return true;
        }
        return copyPatternsFrom;
    }

    @JsonSetter("copyMaterial")
    private void setCopyMaterial(JsonNode node) {
        if (node.isArray()) {
            copyMaterial = false;
            copyMaterialsFrom = new int[node.size()];
            int i = 0;
            for (JsonNode jsonNode : node) {
                copyMaterialsFrom[i++] = jsonNode.asInt();
            }
            return;
        }
        copyMaterial = node.asBoolean();
    }

    @JsonGetter("copyMaterial")
    private Object getCopyMaterial() {
        if (copyMaterial) {
            return true;
        }
        return copyMaterialsFrom;
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        if (result.getItemMeta() instanceof ArmorMeta armorMeta) {
            TrimPattern trimPattern = defaultPattern;
            TrimMaterial trimMaterial = defaultMaterial;
            for (IngredientData ingredientData : recipeData.getBySlots(slots)) {
                var ingredientMeta = ingredientData.itemStack().getItemMeta();
                if (ingredientMeta instanceof ArmorMeta ingredientArmorMeta) {
                    ArmorTrim ingredientTrim = ingredientArmorMeta.getTrim();
                    if (ingredientTrim == null) continue;
                    if (copyPattern || ArrayUtils.contains(copyPatternsFrom, ingredientData.recipeSlot())) {
                        trimPattern = ingredientTrim.getPattern();
                    }
                    if (copyMaterial || ArrayUtils.contains(copyMaterialsFrom, ingredientData.recipeSlot())) {
                        trimMaterial = ingredientTrim.getMaterial();
                    }
                }
            }
            if (trimMaterial == null || trimPattern == null) return result;
            armorMeta.setTrim(new ArmorTrim(trimMaterial, trimPattern));
            result.setItemMeta(armorMeta);
        }
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new ArmorTrimMergeAdapter(this);
    }
}
