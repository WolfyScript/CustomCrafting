package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class TabPlayerHead extends ItemCreatorTabVanilla {

    public static final String KEY = "player_head";

    public TabPlayerHead() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.PLAYER_HEAD, this));
        creator.registerButton(new ItemInputButton<>("player_head.texture.input", Material.AIR, (cache, guiHandler, player, inventory, i, event) -> {
            if (event instanceof InventoryClickEvent) {
                return ((InventoryClickEvent) event).getCurrentItem().getType().equals(Material.PLAYER_HEAD);
            }
            return true;
        }));
        creator.registerButton(new ActionButton<>("player_head.texture.apply", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (inventory.getItem(38) != null && inventory.getItem(38).getType().equals(Material.PLAYER_HEAD)) {
                items.getItem().setPlayerHeadValue(new ItemBuilder(inventory.getItem(38)).getPlayerHeadValue());
            }
            return true;
        }));
        creator.registerButton(new ChatInputButton<>("player_head.owner", Material.NAME_TAG, (guiHandler, player, s, args) -> {
            var itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            if (!(itemMeta instanceof SkullMeta)) {
                return true;
            }
            try {
                var uuid = UUID.fromString(args[0]);
                ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
                guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
            } catch (IllegalArgumentException e) {
                return true;
            }
            return false;
        }));
    }

    @Override
    public boolean shouldRender(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        return items.getItem() != null && item.getType().equals(Material.PLAYER_HEAD);
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(29, "player_head.texture.input");
        update.setButton(30, "player_head.texture.apply");
        update.setButton(32, "player_head.owner");
        update.setButton(36, "meta_ignore.wolfyutilities.playerHead");
    }
}
