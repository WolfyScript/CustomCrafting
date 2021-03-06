package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.util.NamespacedKey;

public class NamespacedKeyUtils {

    public static final String NAMESPACE = "customcrafting";


    public static NamespacedKey fromInternal(NamespacedKey namespacedKey) {
        if (namespacedKey == null) return null;
        return new NamespacedKey(CustomCrafting.getInst(), namespacedKey.toString("/"));
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

}
