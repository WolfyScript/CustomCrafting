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

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemTypeMergeAdapter extends MergeAdapter {

    private static final String TYPE_MAPPINGS = "typeMappings";

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "item_type");

    private final Map<Material, Material> typeMappings;
    private Material defaultType;

    @JsonCreator
    public ItemTypeMergeAdapter(@JsonProperty(TYPE_MAPPINGS) Map<String, String> typeMappings) {
        super(KEY);
        this.typeMappings = readTypeMappings(typeMappings);
    }

    public ItemTypeMergeAdapter(ItemTypeMergeAdapter adapter) {
        super(adapter);
        this.typeMappings = Map.copyOf(adapter.typeMappings);
    }

    @JsonGetter(TYPE_MAPPINGS)
    private Map<String, String> writeTypeMappings() {
        return this.typeMappings.entrySet().stream().map(entry -> Map.entry(entry.getKey().getKey().toString(), entry.getValue().getKey().toString())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<Material, Material> readTypeMappings(Map<String, String> typeMappingsStrings) {
        return typeMappingsStrings.entrySet().stream().map(entry -> {
            Material key = Material.matchMaterial(entry.getKey());
            Material value = Material.matchMaterial(entry.getValue());
            if (key == null || value == null) return null;
            return Map.entry(key, value);
        }).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonIgnore
    public Map<Material, Material> getTypeMappings() {
        return typeMappings;
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        IngredientData ingredientData = recipeData.getBySlot(slots[0]);
        if (ingredientData != null) {
            Material type = ingredientData.itemStack().getType();
            if (typeMappings.isEmpty()) {
                result.setType(type);
            } else {

            }
        }
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new ItemTypeMergeAdapter(this);
    }
}
