package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.OptionButton;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.chat.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabLore extends ItemCreatorTabVanilla {

    public static final String KEY = "lore";

    public TabLore() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register() {
        creator.registerButton(new OptionButton(Material.WRITABLE_BOOK, this));
        creator.registerButton(new ChatInputButton<>(KEY + ".add", Material.WRITABLE_BOOK, (guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getItems().getItem().addLoreLine(s.equals("&empty") ? "" : ChatColor.convert(s));
            return false;
        }));
        creator.registerButton(new ActionButton<>(KEY + ".remove", Material.WRITTEN_BOOK, (cache, guiHandler, player, inventory, i, event) -> {
            ChatUtils.sendLoreManager(player);
            guiHandler.close();
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, KEY + ".add");
        update.setButton(32, KEY + ".remove");
        update.setButton(36, "meta_ignore.wolfyutilities.lore");
    }
}
