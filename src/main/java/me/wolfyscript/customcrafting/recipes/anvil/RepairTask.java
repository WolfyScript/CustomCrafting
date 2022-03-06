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

package me.wolfyscript.customcrafting.recipes.anvil;

import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.recipes.data.AnvilData;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonAutoDetect;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"key", "mode"})
public abstract class RepairTask implements Keyed {

    private final NamespacedKey key;
    @JsonIgnore
    private final CustomRecipeAnvil.Mode mode;

    protected RepairTask(NamespacedKey key, CustomRecipeAnvil.Mode mode) {
        this.mode = mode;
        this.key = key;
    }

    @JsonIgnore
    public CustomRecipeAnvil.Mode getMode() {
        return mode;
    }

    public abstract CustomItem computeResult(CustomRecipeAnvil recipe, PrepareAnvilEvent event, AnvilData anvilData, Player player, ItemStack inputLeft, ItemStack inputRight);

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }
}
