/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TabDisplayName extends ItemCreatorTabVanilla {

    public static final String KEY = "display_name";

    public TabDisplayName() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        ButtonOption.register(creator.getButtonBuilder(), Material.NAME_TAG, this);
        creator.getButtonBuilder().chatInput(KEY + ".set")
                .inputAction((guiHandler, player, s, strings) -> {
                    if (creator.getCustomCrafting().isPaper()) {
                        CustomItem customItem = guiHandler.getCustomCache().getItems().getItem();
                        ItemMeta itemMeta = customItem.getItemStack().getItemMeta();
                        // Need to use the non-relocated MiniMessage! TODO: v5.0 | No longer shade & relocate Adventure!
                        itemMeta.displayName(MiniMessage.miniMessage().deserialize(s));
                        customItem.setItemMeta(itemMeta);
                    } else {
                        guiHandler.getCustomCache().getItems().getItem().setDisplayName(BukkitComponentSerializer.legacy().serialize(api.getChat().getMiniMessage().deserialize(s)));
                    }
                    return false;
                }).state(state -> state.icon(Material.GREEN_CONCRETE).action((cache, guiHandler, player, guiInventory, btn, i, event) -> {
                    var chat = guiInventory.getWindow().getChat();
                    chat.sendMessage(player, chat.translated("msg.input.wui_command"));
                    chat.sendMessage(player, chat.translated("msg.input.mini_message"));
                    return true;
                })).register();
        creator.getButtonBuilder().action(KEY + ".remove").state(state -> state.icon(Material.RED_CONCRETE).action((cache, guiHandler, player, inventory, btn, i, event) -> {
            cache.getItems().getItem().setDisplayName(null);
            return true;
        })).register();
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, KEY + ".set");
        update.setButton(32, KEY + ".remove");
    }
}
