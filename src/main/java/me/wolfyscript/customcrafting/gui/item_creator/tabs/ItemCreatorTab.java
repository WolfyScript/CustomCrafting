package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class ItemCreatorTab implements Keyed {

    protected final NamespacedKey key;

    ItemCreatorTab(NamespacedKey key) {
        this.key = key;
    }

    public abstract void register(MenuItemCreator creator, WolfyUtilities api);

    public abstract void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item);

    public boolean shouldRender(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        return true;
    }

    public String getOptionButton() {
        return key.getKey() + ".option";
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }
}
