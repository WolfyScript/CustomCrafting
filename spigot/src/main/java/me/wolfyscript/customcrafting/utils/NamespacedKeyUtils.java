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


    /**
     * Converts the given internal namespaced key to the original key.<br>
     * <pre>&lt;namespace&gt;:&lt;key&gt; -> &lt;customcrafting&gt;:&lt;namespace&gt;/&lt;key&gt;</pre>
     *
     * @param internalKey The internal namespaced key.
     * @return The original namespaced key.
     */
    @Deprecated(since = "3.16.1.0")
    public static NamespacedKey fromInternal(NamespacedKey internalKey) {
        if (internalKey == null) return null;
        return new NamespacedKey(NAMESPACE, internalKey.toString("/"));
    }

    /**
     * Converts the given namespaced key to the internal key.<br>
     * <pre>&lt;customcrafting&gt;:&lt;namespace&gt;/&lt;key&gt; -> &lt;namespace&gt;:&lt;key&gt;</pre>
     *
     * @param namespacedKey The namespaced key to convert.
     * @return The internal namespaced key in the registry.
     * @deprecated This is mostly bad design. Recipes should always be registered using the plugin namespace. In the future that will also apply to customcrafting recipes!
     */
    @Deprecated(since = "3.16.1.0")
    public static NamespacedKey toInternal(NamespacedKey namespacedKey) {
        if (namespacedKey != null && namespacedKey.getNamespace().equals(NAMESPACE)) {
            String[] values = namespacedKey.getKey().split("/", 2);
            if (values.length > 1) {
                return new NamespacedKey(values[0], values[1]);
            }
        }
        return namespacedKey;
    }

    /**
     * @deprecated Use {@link #getKeyRoot(NamespacedKey)} instead!
     */
    @Deprecated
    public static String getInternalNamespace(NamespacedKey namespacedKey) {
        if (namespacedKey.getNamespace().equals(NAMESPACE)) {
            String[] values = namespacedKey.getKey().split("/", 2);
            if (values.length > 1) {
                return values[0];
            }
        }
        return null;
    }

    /**
     * Gets the root of the NamespacedKeys' key.
     * That means the first folder separated by a "/", if one is available, is returned.
     * In case the key has no folders then an empty String is returned.
     * <pre>
     *     "namespace:root_folder/sub_folder/object" -> "root_folder"
     *     "namespace:root_folder/object" -> "root_folder"
     *     "namespace:object" -> ""
     * </pre>
     *
     * @param namespacedKey The NamespacedKey
     * @return The root folder, that is separated by "/", of the key; Otherwise if no folder exists, an empty String.
     */
    public static String getKeyRoot(NamespacedKey namespacedKey) {
        return namespacedKey.getKey().contains("/") ? namespacedKey.getKey().split("/", 2)[0] : "";
    }

    /**
     * Gets the object of the NamespacedKeys' key.
     * That means the last part separated by a "/", if one is available, is returned.
     * In case the key has no folders then it returns the key as is.
     * <pre>
     *     "namespace:root_folder/sub_folder/object" -> "object"
     *     "namespace:object" -> "object"
     * </pre>
     *
     * @param namespacedKey The NamespacedKey
     * @return The root folder, that is separated by "/", of the key; Otherwise if no folder exists, the key as is.
     */
    public static String getKeyObj(NamespacedKey namespacedKey) {
        String key = namespacedKey.getKey();
        if (key.contains("/")) {
            String[] parts = key.split("/");
            return parts[parts.length - 1];
        }
        return key;
    }

    /**
     * Gets the path to the object of the NamespacedKeys' key relative to the root (see {@link #getKeyRoot(NamespacedKey)}).<br>
     * That means the part after the root folder.<br>
     * In case the key has no folders then it returns an empty String.
     * <p>
     * <code>
     * "namespace:root_folder/sub_folder/object" -> "sub_folder/object"<br>
     * "namespace:root_folder/first/another/object" -> "first/another/object"<br>
     * "namespace:root_folder/object" -> "object"<br>
     * "namespace:object" -> ""<br>
     * </code>
     * </p>
     *
     * @param namespacedKey The NamespacedKey
     * @return The path to the object, that is separated by "/"; Otherwise if no folder exists, an empty String.
     */
    public static String getRelativeKeyObjPath(NamespacedKey namespacedKey) {
        String key = namespacedKey.getKey();
        int firstIndex = key.indexOf("/") + 1;
        if (firstIndex > 0) {
            return key.substring(firstIndex);
        }
        return "";
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
