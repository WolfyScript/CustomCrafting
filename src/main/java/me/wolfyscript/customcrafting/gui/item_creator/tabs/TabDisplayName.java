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

import com.wolfyscript.utilities.bukkit.world.items.reference.BukkitStackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.WolfyUtilsStackIdentifier;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TabDisplayName extends ItemCreatorTabVanilla {

    public static final String KEY = "display_name";

    public TabDisplayName() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.NAME_TAG, this));
        new ChatInputButton.Builder<>(creator, KEY + ".set")
                .inputAction((guiHandler, player, s, strings) -> {
                    guiHandler.getCustomCache().getItems().modifyOriginalStack(itemStack -> {
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (creator.getCustomCrafting().isPaper()) {
                            // TODO: Need to use the non-relocated MiniMessage! (v5) No longer shade & relocate Adventure
                            itemMeta.displayName(MiniMessage.miniMessage().deserialize(s));
                        } else {
                            itemMeta.setDisplayName(BukkitComponentSerializer.legacy().serialize(api.getChat().getMiniMessage().deserialize(s)));
                        }
                        itemStack.setItemMeta(itemMeta);
                    });
                    return false;
                }).state(state -> state.icon(Material.GREEN_CONCRETE).action((cache, guiHandler, player, guiInventory, i, event) -> {
                    var chat = guiInventory.getWindow().getChat();
                    chat.sendMessage(player, chat.translated("msg.input.wui_command"));
                    chat.sendMessage(player, chat.translated("msg.input.mini_message"));
                    return true;
                })).register();
        creator.registerButton(new ActionButton<>(KEY + ".remove", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, i, event) -> {
            guiHandler.getCustomCache().getItems().getItem().setDisplayName(null);
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, KEY + ".set");
        update.setButton(32, KEY + ".remove");
    }
}
