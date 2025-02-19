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

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemTypeMergeAdapter extends MergeAdapter {

    private static final String TYPE_MAPPINGS = "typeMappings";
    private static final String DEFAULT_TYPE = "defaultType";

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "item");

    private Map<Material, Material> typeMappings;
    private Material defaultType;

    @JsonCreator
    public ItemTypeMergeAdapter() {
        super(KEY);
        this.typeMappings = Collections.emptyMap();
    }

    public ItemTypeMergeAdapter(ItemTypeMergeAdapter adapter) {
        super(adapter);
        this.typeMappings = Map.copyOf(adapter.typeMappings);
    }

    @JsonSetter(DEFAULT_TYPE)
    private void setDefaultType(String defaultType) {
        this.defaultType = defaultType == null ? null : Material.matchMaterial(defaultType);
    }

    @JsonIgnore
    public static String getDefaultType() {
        return DEFAULT_TYPE;
    }

    @JsonGetter(DEFAULT_TYPE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String writeDefaultType() {
        return defaultType == null ? null : defaultType.getKey().toString();
    }

    @JsonGetter(TYPE_MAPPINGS)
    private Map<String, String> writeTypeMappings() {
        return this.typeMappings.entrySet().stream().map(entry -> Map.entry(entry.getKey().getKey().toString(), entry.getValue().getKey().toString())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonSetter(TYPE_MAPPINGS)
    private void readTypeMappings(Map<String, String> typeMappingsStrings) {
        if (typeMappingsStrings == null) {
            this.typeMappings = Collections.emptyMap();
            return;
        }
        this.typeMappings = typeMappingsStrings.entrySet().stream().map(entry -> {
            Material key = Material.matchMaterial(entry.getKey());
            Material value = Material.matchMaterial(entry.getValue());
            if (key == null || value == null) return null;
            return Map.entry(key, value);
        }).filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonIgnore
    public Map<Material, Material> getTypeMappings() {
        return typeMappings;
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, StackReference resultReference, ItemStack result) {
        recipeData.bySlot(slots[0]).ifPresent(ingredientData -> {
            if (ingredientData.itemStack() == null) {
                if (defaultType != null) {
                    result.setType(defaultType);
                }
                return;
            }
            Material type = ingredientData.itemStack().getType();
            if (typeMappings.isEmpty()) {
                result.setType(type);
            } else {
                Material mappedType = typeMappings.get(type);
                result.setType(Objects.requireNonNullElseGet(mappedType, () -> Objects.requireNonNullElse(defaultType, type)));
            }
        });
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new ItemTypeMergeAdapter(this);
    }
}
