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

package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.utils.chat.CollectionEditor;
import me.wolfyscript.lib.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.MiniMessage;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.chat.ClickEvent;
import me.wolfyscript.utilities.api.chat.HoverEvent;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.util.NamespacedKey;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class ChatUtils {

    private static final WolfyUtilities api = CustomCrafting.inst().getApi();
    private static final Chat chat = api.getChat();
    private static final MiniMessage miniM = chat.getMiniMessage();
    private final CustomCrafting customCrafting;

    public ChatUtils(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    public static boolean checkPerm(CommandSender sender, String perm) {
        return checkPerm(sender, perm, true);
    }

    public static boolean checkPerm(CommandSender sender, String perm, boolean sendMessage) {
        if (sender.hasPermission(perm)) {
            return true;
        }
        if (sendMessage) {
            var msg = chat.translated("msg.denied_perm", Placeholder.unparsed("perm", perm));
            if (sender instanceof Player player) {
                chat.sendMessage(player, msg);
            } else {
                api.getConsole().severe(BukkitComponentSerializer.legacy().serialize(msg));
            }
        }
        return false;
    }

    /**
     * @param player The player that inputted the values.
     * @param s      The input as a String
     * @param args   The input as separate arguments (split by spaces)
     * @return The {@link NamespacedKey} used internally in CustomCrafting in the format <b><i>namespace</i>:<i>key</i></b>
     */
    @Deprecated
    public static NamespacedKey getInternalNamespacedKey(Player player, String s, String[] args) {
        try {
            if (args.length > 1) {
                return new NamespacedKey(args[0], args[1]);
            }
            return s.contains(":") ? NamespacedKey.of(s) : null;
        } catch (IllegalArgumentException e) {
            api.getLanguageAPI().getComponents("msg.player.invalid_namespacedkey").forEach(s1 -> chat.sendMessage(player, s1));
        }
        return null;
    }

    /**
     * @param player The player that inputted the values.
     * @param s      The input as a String
     * @param args   The input as separate arguments (split by spaces)
     * @return The {@link NamespacedKey} used in WolfyUtilities, to identify which plugin it's from, in the format <b>customcrafting:<i>namespace</i>/<i>key</i></b>
     */
    public static NamespacedKey getNamespacedKey(Player player, String s, String[] args) {
        try {
            String folder;
            String fileName;
            if (args.length > 1) {
                folder = args[0];
                fileName = args[1];
            } else if (s.contains(":")) {
                NamespacedKey key = NamespacedKey.of(s);
                if (key == null) return null;
                folder = key.getNamespace();
                fileName = key.getKey();
            } else {
                return null;
            }
            if (!folder.endsWith("/")) {
                // Add separator between directory and object
                folder += "/";
            }
            if (folder.startsWith("/")) {
                // Do not include the root in the namespaced key!
                folder = folder.substring(1);
            }
            if (fileName.startsWith("/")) {
                // We added the separator to the directory already!
                fileName = fileName.substring(1);
            }
            if (!folder.isBlank()) {
                return new NamespacedKey(CustomCrafting.inst(), folder + fileName);
            }
            // Cannot save inside Root Directory! Requires at least one directory.
            api.getLanguageAPI().getComponents("msg.player.invalid_namespacedkey").forEach(s1 -> chat.sendMessage(player, s1));
        } catch (IllegalArgumentException e) {
            api.getLanguageAPI().getComponents("msg.player.invalid_namespacedkey").forEach(s1 -> chat.sendMessage(player, s1));
        }
        return null;
    }

    @Deprecated
    public static void sendLoreManager(Player player) {
        createLoreChatEditor(api.getInventoryAPI(CCCache.class)).send(player);
    }

    public static CollectionEditor<CCCache, String> createLoreChatEditor(InventoryAPI<CCCache> invAPI) {
        return new CollectionEditor<CCCache, String>(invAPI,
                (guiHandler, player, cache) -> {
                    var itemMeta = cache.getItems().asBukkitIdentifier().map(identifier -> identifier.stack().getItemMeta()).orElse(null);
                    return itemMeta != null && itemMeta.hasLore() ? itemMeta.getLore() : List.of();
                },
                (guiHandler, player, cache, line) -> BukkitComponentSerializer.legacy().deserialize(line),
                (guiHandler, player, cache, msg, args) -> BukkitComponentSerializer.legacy().serialize(miniM.deserialize(msg))
        ).onAdd((guiHandler, player, cache, index, entry) -> cache.getItems().modifyOriginalStack(stack -> {
            var itemMeta = stack.getItemMeta();
            List<String> lore = itemMeta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            if (index >= 0) {
                lore.add(index, entry);
            } else {
                lore.add(entry);
            }
            itemMeta.setLore(lore);
            stack.setItemMeta(itemMeta);
        })).onRemove((guiHandler, player, cache, index, entry) -> cache.getItems().modifyOriginalStack(stack -> {
            var itemMeta = stack.getItemMeta();
            List<String> lore = itemMeta.getLore();
            if (lore != null) {
                lore.remove(index);
                itemMeta.setLore(lore);
            }
            stack.setItemMeta(itemMeta);
        })).onMove((guiHandler, player, cache, fromIndex, toIndex) -> cache.getItems().modifyOriginalStack(stack -> {
            var itemMeta = stack.getItemMeta();
            List<String> lore = itemMeta.getLore();
            if (lore != null) {
                String toPrevEntry = lore.get(toIndex);
                lore.set(toIndex, lore.get(fromIndex));
                lore.set(fromIndex, toPrevEntry);
                itemMeta.setLore(lore);
            }
            stack.setItemMeta(itemMeta);
        })).onEdit((guiHandler, player, cache, index, previousEntry, newEntry) -> cache.getItems().modifyOriginalStack(stack -> {
            var itemMeta = stack.getItemMeta();
            List<String> lore = itemMeta.getLore();
            if (lore != null) {
                lore.set(index, newEntry);
                itemMeta.setLore(lore);
            }
            stack.setItemMeta(itemMeta);
        })).setSendInputInfoMessages((guiHandler, player, cache) -> {
            var chat = invAPI.getWolfyUtilities().getChat();
            chat.sendMessage(player, chat.translated("msg.input.wui_command"));
            chat.sendMessage(player, chat.translated("msg.input.mini_message"));
        });
    }

    public static CollectionEditor<CCCache, net.kyori.adventure.text.Component> createPaperLoreChatEditor(InventoryAPI<CCCache> invAPI) {
        var paperMiniMsg = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage();
        return new CollectionEditor<CCCache, net.kyori.adventure.text.Component>(invAPI,
                (guiHandler, player, cache) -> {
                    var itemMeta = cache.getItems().asBukkitIdentifier().map(identifier -> identifier.stack().getItemMeta()).orElse(null);
                    return itemMeta != null && itemMeta.hasLore() ? itemMeta.lore() : List.of();
                },
                // TODO: This is quite inefficient! We need to convert to the non-shaded version of Adventure! (v5)
                (guiHandler, player, cache, line) -> miniM.deserialize(paperMiniMsg.serialize(line)),
                (guiHandler, player, cache, msg, args) -> paperMiniMsg.deserialize(msg)
        ).onAdd((guiHandler, player, cache, index, entry) -> cache.getItems().modifyOriginalStack(stack -> {
            var itemMeta = stack.getItemMeta();
            List<Component> lore = itemMeta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            if (index >= 0) {
                lore.add(index, entry);
            } else {
                lore.add(entry);
            }
            itemMeta.lore(lore);
            stack.setItemMeta(itemMeta);
        })).onRemove((guiHandler, player, cache, index, entry) -> cache.getItems().modifyOriginalStack(stack -> {
            var itemMeta = stack.getItemMeta();
            List<Component> lore = itemMeta.lore();
            if (lore != null) {
                lore.remove(index);
                itemMeta.lore(lore);
            }
            stack.setItemMeta(itemMeta);
        })).onMove((guiHandler, player, cache, fromIndex, toIndex) -> cache.getItems().modifyOriginalStack(stack -> {
            var itemMeta = stack.getItemMeta();
            List<Component> lore = itemMeta.lore();
            if (lore != null) {
                Component toPrevEntry = lore.get(toIndex);
                lore.set(toIndex, lore.get(fromIndex));
                lore.set(fromIndex, toPrevEntry);
                itemMeta.lore(lore);
            }
            stack.setItemMeta(itemMeta);
        })).onEdit((guiHandler, player, cache, index, previousEntry, newEntry) -> cache.getItems().modifyOriginalStack(stack -> {
            var itemMeta = stack.getItemMeta();
            List<Component> lore = itemMeta.lore();
            if (lore != null) {
                lore.set(index, newEntry);
                itemMeta.lore(lore);
            }
            stack.setItemMeta(itemMeta);
        })).setSendInputInfoMessages((guiHandler, player, cache) -> {
            var chat = invAPI.getWolfyUtilities().getChat();
            chat.sendMessage(player, chat.translated("msg.input.wui_command"));
            chat.sendMessage(player, chat.translated("msg.input.mini_message"));
        });
    }

    public static void sendAttributeModifierManager(Player player) {
        CCCache cache = ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache());
        var items = cache.getItems();
        items.modifyOriginalStack(stack -> {
            var itemMeta = stack.getItemMeta();
            for (int i = 0; i < 15; i++) {
                player.sendMessage("");
            }
            chat.sendMessage(player, "-----------------[&cRemove Modifier&7]-----------------");
            chat.sendMessage(player, "");
            if (itemMeta.hasAttributeModifiers()) {
                Collection<AttributeModifier> modifiers = itemMeta.getAttributeModifiers(Attribute.valueOf(cache.getSubSetting().substring("attribute.".length()).toUpperCase(Locale.ROOT)));
                if (modifiers != null) {
                    chat.sendMessage(player, "        §e§oName   §b§oAmount  §6§oEquipment-Slot  §3§oMode  §7§oUUID");
                    chat.sendMessage(player, "");
                    for (AttributeModifier modifier : modifiers) {
                        chat.sendActionMessage(player,
                                new ClickData("§7[§c-§7] ", (wolfyUtilities, player1) -> {
                                    CCCache cache1 = (CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache();
                                    itemMeta.removeAttributeModifier(Attribute.valueOf(cache1.getSubSetting().substring("attribute.".length()).toUpperCase(Locale.ROOT)), modifier);
                                    stack.setItemMeta(itemMeta);
                                    sendAttributeModifierManager(player1);
                                }, new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, "§c" + modifier.getName())),
                                new ClickData("§e" + modifier.getName() + "  §b" + modifier.getAmount() + "  §6" + (modifier.getSlot() == null ? "ANY" : modifier.getSlot()) + "  §3" + modifier.getOperation(), null),
                                new ClickData("  ", null),
                                new ClickData("§7[UUID]", null, new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, "§7[§3Click to copy§7]\n" + modifier.getUniqueId()), new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "" + modifier.getUniqueId()))
                        );
                    }
                } else {
                    chat.sendMessage(player, ChatColor.RED + "No attributes set yet!");
                }
            }
            chat.sendMessage(player, "");
            chat.sendMessage(player, "-------------------------------------------------");
            chat.sendActionMessage(player, new ClickData("                     §7[§3Back to ItemCreator§7]", (wolfyUtilities, player1) -> {
                for (int i = 0; i < 15; i++) {
                    player.sendMessage("");
                }
                api.getInventoryAPI().getGuiHandler(player1).openCluster();
            }, true));
        });
    }

    public static void sendRecipeItemLoadingError(String prefix, String namespace, String key, Exception ex) {
        sendRecipeItemLoadingError(prefix, namespace, key, ex, CustomCrafting.inst().getConfigHandler().getConfig().getDataSettings().printStackTrace());
    }

    public static void sendRecipeItemLoadingError(String prefix, String namespace, String key, Exception ex, boolean stacktrace) {
        CustomCrafting.inst().getLogger().warning(prefix + "[Error] Invalid Recipe: \"" + namespace + ":" + key + "\": " + (ex.getMessage() != null ? ex.getMessage() : ""));
        if (ex.getCause() != null) {
            CustomCrafting.inst().getLogger().warning(prefix + "[Error]   Caused by: " + ex.getCause().getMessage());
        }
        if (stacktrace) {
            CustomCrafting.inst().getLogger().log(Level.WARNING, "------------------[StackTrace]-------------------");
            CustomCrafting.inst().getLogger().log(Level.WARNING, "", ex);
        }
    }
}
