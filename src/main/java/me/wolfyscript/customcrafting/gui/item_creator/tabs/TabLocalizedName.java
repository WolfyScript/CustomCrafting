package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.OptionButton;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.chat.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabLocalizedName extends ItemCreatorTab {

    public static final String KEY = "localized_name";

    public TabLocalizedName() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register() {
        creator.registerButton(new OptionButton(Material.NAME_TAG, this));
        creator.registerButton(new ChatInputButton<>("localized_name.set", Material.NAME_TAG, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getItemMeta().getLocalizedName());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            var itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            itemMeta.setLocalizedName(ChatColor.convert(s));
            guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
            return false;
        }));
        creator.registerButton(new ActionButton<>("localized_name.remove", Material.NAME_TAG, (cache, guiHandler, player, inventory, i, inventoryClickEvent) -> {
            var itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            itemMeta.setLocalizedName(null);
            guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "localized_name.set");
        update.setButton(32, "localized_name.remove");
    }
}
