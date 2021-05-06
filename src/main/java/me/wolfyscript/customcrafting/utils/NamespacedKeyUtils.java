package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.world.BlockCustomItemStore;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class NamespacedKeyUtils {

    private NamespacedKeyUtils() {
    }

    public static final String NAMESPACE = "customcrafting";

    public static NamespacedKey fromInternal(NamespacedKey internalKey) {
        if (internalKey == null) return null;
        return new NamespacedKey(NAMESPACE, internalKey.toString("/"));
    }

    public static NamespacedKey toInternal(NamespacedKey namespacedKey) {
        if (namespacedKey.getNamespace().equals(NAMESPACE)) {
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

    public static CustomItem getCustomItem(Block block) {
        return getCustomItem(block.getLocation());
    }

    public static CustomItem getCustomItem(Location location) {
        return getCustomItem(WorldUtils.getWorldCustomItemStore().get(location));
    }

    public static CustomItem getCustomItem(BlockCustomItemStore store) {
        if (store != null) {
            CustomItem customItem = store.getCustomItem();
            if (customItem == null) {
                customItem = Registry.CUSTOM_ITEMS.get(fromInternal(store.getCustomItemKey()));
            }
            return customItem;
        }
        return null;
    }

}
