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

package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.item_creator.ClusterItemCreator;
import me.wolfyscript.customcrafting.gui.recipe_creator.ClusterRecipeCreator;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.lib.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

class ButtonSelectCustomItem extends ActionButton<CCCache> {

    ButtonSelectCustomItem(CustomCrafting customCrafting, NamespacedKey namespacedKey) {
        super("item_" + namespacedKey.toString("__"), new ButtonState<>("custom_item_error", Material.STONE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            var registry = guiHandler.getApi().getRegistries().getCustomItems();
            if (!registry.has(namespacedKey) || ItemUtils.isAirOrNull(registry.get(namespacedKey))) {
                return true;
            }
            WolfyUtilities api = customCrafting.getApi();
            var chat = api.getChat();
            var customItem = registry.get(namespacedKey);
            if (event instanceof InventoryClickEvent clickEvent) {
                var currentMenu = guiHandler.getWindow();
                var itemEditor = guiHandler.getInvAPI().getGuiWindow(ClusterRecipeCreator.ITEM_EDITOR);
                assert currentMenu != null;
                if (clickEvent.isRightClick()) {
                    if (clickEvent.isShiftClick()) {
                        currentMenu.sendMessage(guiHandler, currentMenu.translatedMsgKey("delete.confirm", Placeholder.unparsed("item", customItem.getNamespacedKey().toString())));
                        currentMenu.sendMessage(guiHandler, chat.translated("inventories.none.item_list.messages.delete.confirmed").clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                            guiHandler.openCluster();
                            Bukkit.getScheduler().runTaskAsynchronously(customCrafting, () -> ItemLoader.deleteItem(namespacedKey, player));
                        })).append(chat.translated("inventories.none.item_list.messages.delete.declined").clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> guiHandler.openCluster()))));
                    } else if (customItem != null) {
                        items.setItem(items.isRecipeItem(), customItem.clone());
                        itemEditor.sendMessage(guiHandler, itemEditor.translatedMsgKey("item_editable"));
                        guiHandler.openWindow(ClusterItemCreator.MAIN_MENU);
                    }
                } else if (clickEvent.isLeftClick()) {
                    if (cache.getSetting().equals(Setting.RECIPE_CREATOR)) {
                        cache.applyItem(customItem);
                        itemEditor.sendMessage(guiHandler, itemEditor.translatedMsgKey("item_applied"));
                        List<? extends GuiWindow<?>> history = guiHandler.getClusterHistory().get(guiHandler.getCluster());
                        history.remove(history.size() - 1);
                        guiHandler.openCluster(ClusterRecipeCreator.KEY);
                    } else if (ChatUtils.checkPerm(player, "customcrafting.cmd.give")) {
                        var itemStack = customItem.create();
                        int amount = clickEvent.isShiftClick() ? itemStack.getMaxStackSize() : 1;
                        itemStack.setAmount(amount);
                        if (InventoryUtils.hasInventorySpace(player, itemStack)) {
                            player.getInventory().addItem(itemStack);
                        } else {
                            player.getLocation().getWorld().dropItem(player.getLocation(), itemStack);
                        }
                        var playerPlaceHolder = Placeholder.unparsed("player", player.getDisplayName());
                        var itemPlaceholder = Placeholder.unparsed("item", namespacedKey.toString());
                        if (clickEvent.isShiftClick()) {
                            currentMenu.sendMessage(guiHandler, chat.translated("commands.give.success_amount", playerPlaceHolder, itemPlaceholder, Placeholder.unparsed("amount", String.valueOf(amount))));
                        } else {
                            currentMenu.sendMessage(guiHandler, chat.translated("commands.give.success", playerPlaceHolder, itemPlaceholder));
                        }
                    }
                }
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var api = guiHandler.getApi();
            var customItem = api.getRegistries().getCustomItems().get(namespacedKey);
            if (!ItemUtils.isAirOrNull(customItem)) {
                var itemB = new ItemBuilder(customItem.create());
                itemB.addLoreLine("");
                itemB.addLoreLine(org.bukkit.ChatColor.DARK_GRAY.toString() + namespacedKey);
                api.getLanguageAPI().getComponents("inventories.none.item_list.items.custom_item.lore").forEach(c -> itemB.addLoreLine(BukkitComponentSerializer.legacy().serialize(c)));
                return itemB.create();
            }
            var itemB = new ItemBuilder(itemStack);
            itemB.addLoreLine("");
            itemB.addLoreLine(org.bukkit.ChatColor.DARK_GRAY.toString() + namespacedKey);
            itemB.addLoreLine("");
            return itemB.create();
        }));
    }

}
