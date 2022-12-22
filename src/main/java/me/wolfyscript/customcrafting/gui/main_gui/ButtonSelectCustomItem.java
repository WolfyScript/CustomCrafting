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

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.InventoryUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import java.util.List;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.item_creator.ClusterItemCreator;
import me.wolfyscript.customcrafting.gui.recipe_creator.ClusterRecipeCreator;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

class ButtonSelectCustomItem {

    public static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, CustomCrafting customCrafting, NamespacedKey namespacedKey) {
        buttonBuilder.action("item_" + namespacedKey.toString("__")).state(state -> state.key("custom_item_error").icon(Material.STONE).action((cache, guiHandler, player, inventory, btn, i, event) -> {
            var items = cache.getItems();
            var registry = guiHandler.getWolfyUtils().getRegistries().getCustomItems();
            if (!registry.has(namespacedKey) || ItemUtils.isAirOrNull(registry.get(namespacedKey))) {
                return true;
            }
            WolfyUtilsBukkit api = customCrafting.getApi();
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
                        List<? extends GuiWindow<?>> history = guiHandler.getHistory(guiHandler.getCluster());
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
        }).render((cache, guiHandler, player, inventory, btn, itemStack, help) -> {
            var api = guiHandler.getWolfyUtils();
            var customItem = api.getRegistries().getCustomItems().get(namespacedKey);
            if (!ItemUtils.isAirOrNull(customItem)) {
                var itemB = new ItemBuilder(customItem.create());
                itemB.addLoreLine("");
                itemB.addLoreLine(org.bukkit.ChatColor.DARK_GRAY.toString() + namespacedKey);
                api.getLanguageAPI().getComponents("inventories.none.item_list.items.custom_item.lore").forEach(c -> itemB.addLoreLine(BukkitComponentSerializer.legacy().serialize(c)));
                return CallbackButtonRender.UpdateResult.of(itemB.create());
            }
            var itemB = new ItemBuilder(itemStack);
            itemB.addLoreLine("");
            itemB.addLoreLine(org.bukkit.ChatColor.DARK_GRAY.toString() + namespacedKey);
            itemB.addLoreLine("");
            return CallbackButtonRender.UpdateResult.of(itemB.create());
        })).register();
    }

}
