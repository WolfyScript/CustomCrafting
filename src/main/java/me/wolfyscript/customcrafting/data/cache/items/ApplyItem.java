package me.wolfyscript.customcrafting.data.cache.items;

import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

public interface ApplyItem {

    void applyItem(Items items, PlayerCache cache, CustomItem customItem);
}
