package me.wolfyscript.customcrafting.data.cache.items;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

public interface ApplyItem {

    void applyItem(Items items, TestCache cache, CustomItem customItem);
}
