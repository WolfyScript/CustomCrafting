package me.wolfyscript.customcrafting.data.cache.items;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

public interface ApplyItem {

    void applyItem(Items items, CCCache cache, CustomItem customItem);
}
