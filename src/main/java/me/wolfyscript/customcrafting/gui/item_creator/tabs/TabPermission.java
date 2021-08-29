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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabPermission extends ItemCreatorTab {

    public static final String KEY = "permission";

    public TabPermission() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.BARRIER, this));
        creator.registerButton(new ChatInputButton<>("permission.set", Material.GREEN_CONCRETE, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            String perm = guiHandler.getCustomCache().getItems().getItem().getPermission();
            hashMap.put("%VAR%", perm.isEmpty() ? "none" : perm);
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getItems().getItem().setPermission(s.replace(" ", "."));
            return false;
        }));
        creator.registerButton(new ActionButton<>("permission.remove", Material.RED_CONCRETE_POWDER, (cache, guiHandler, player, inventory, i, event) -> {
            guiHandler.getCustomCache().getItems().getItem().setPermission("");
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "permission.set");
        update.setButton(32, "permission.remove");
    }
}
