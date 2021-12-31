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

package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.world.BlockCustomItemStore;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NamespacedKeyUtils {

    private NamespacedKeyUtils() {
    }

    public static final String NAMESPACE = "customcrafting";

    public static NamespacedKey fromInternal(NamespacedKey internalKey) {
        if (internalKey == null) return null;
        return new NamespacedKey(NAMESPACE, internalKey.toString("/"));
    }

    public static NamespacedKey toInternal(NamespacedKey namespacedKey) {
        if (namespacedKey != null && namespacedKey.getNamespace().equals(NAMESPACE)) {
            String[] values = namespacedKey.getKey().split("/", 2);
            if (values.length > 1) {
                return new NamespacedKey(values[0], values[1]);
            }
        }
        return namespacedKey;
    }

    public static String getInternalNamespace(NamespacedKey namespacedKey) {
        if (namespacedKey.getNamespace().equals(NAMESPACE)) {
            String[] values = namespacedKey.getKey().split("/", 2);
            if (values.length > 1) {
                return values[0];
            }
        }
        return null;
    }

    public static boolean partiallyMatches(String token, NamespacedKey namespacedKey) {
        return (!token.contains(":") && namespacedKey.getKey().startsWith(token)) || namespacedKey.toString().startsWith(token);
    }

    public static List<NamespacedKey> getPartialMatches(String token, Collection<NamespacedKey> originals) {
        return originals.stream().filter(nKey -> partiallyMatches(token, nKey)).collect(Collectors.toList());
    }

    public static CustomItem getCustomItem(Block block) {
        return getCustomItem(block.getLocation());
    }

    public static CustomItem getCustomItem(Location location) {
        return getCustomItem(WorldUtils.getWorldCustomItemStore().get(location));
    }

    public static CustomItem getCustomItem(BlockCustomItemStore store) {
        if (store != null) {
            var customItem = store.getCustomItem();
            if (customItem == null) {
                customItem = WolfyUtilCore.getInstance().getRegistries().getCustomItems().get(fromInternal(store.getCustomItemKey()));
            }
            return customItem;
        }
        return null;
    }

}
