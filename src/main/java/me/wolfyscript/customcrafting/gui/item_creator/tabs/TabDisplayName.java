package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.chat.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabDisplayName extends ItemCreatorTabVanilla {

    public static final String KEY = "display_name";

    public TabDisplayName() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.NAME_TAG, this));
        creator.registerButton(new ChatInputButton<>(KEY + ".set", Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getItems().getItem().setDisplayName(ChatColor.convert(s));
            return false;
        }));
        creator.registerButton(new ActionButton<>(KEY + ".remove", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, i, event) -> {
            guiHandler.getCustomCache().getItems().getItem().setDisplayName(null);
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, KEY + ".set");
        update.setButton(32, KEY + ".remove");
        update.setButton(36, "meta_ignore.wolfyutilities.name");
    }
}
