package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.ItemCreatorCluster;
import me.wolfyscript.customcrafting.gui.item_creator.ItemCreator;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class ItemCreatorTab implements Keyed {

    protected final NamespacedKey key;
    protected final ItemCreator creator;
    protected final WolfyUtilities api;

    ItemCreatorTab(NamespacedKey key) {
        this.key = key;
        this.creator = (ItemCreator) CustomCrafting.inst().getApi().getInventoryAPI().getGuiWindow(ItemCreatorCluster.MAIN_MENU);
        this.api = this.creator.getWolfyUtilities();
    }

    public abstract void register();

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
