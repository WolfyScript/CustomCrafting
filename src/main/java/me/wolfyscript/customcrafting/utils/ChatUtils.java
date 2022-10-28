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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.lib.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.format.NamedTextColor;
import me.wolfyscript.lib.net.kyori.adventure.text.format.TextDecoration;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.MiniMessage;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.chat.ClickEvent;
import me.wolfyscript.utilities.api.chat.HoverEvent;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                folder += "/";
            }
            if (fileName.startsWith("/")) {
                fileName = fileName.substring(1);
            }
            return new NamespacedKey(CustomCrafting.inst(), folder + fileName);
        } catch (IllegalArgumentException e) {
            api.getLanguageAPI().getComponents("msg.player.invalid_namespacedkey").forEach(s1 -> chat.sendMessage(player, s1));
        }
        return null;
    }

    public static void sendCategoryDescription(Player player) {
        List<String> description = ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getRecipeBookEditor().getCategorySetting().getDescription();
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        chat.sendMessage(player, "------------------[&cEdit Description&7]-----------------");
        chat.sendMessage(player, "");
        if (!description.isEmpty()) {
            int i = 0;
            for (String line : description) {
                int finalI = i;
                chat.sendActionMessage(player, new ClickData("§7[§4-§7] ", (wolfyUtilities, player1) -> {
                    description.remove(finalI);
                    sendCategoryDescription(player1);
                }, true), new ClickData(line, null));
                i++;
            }
        } else {
            chat.sendMessage(player, ChatColor.BOLD.toString() + ChatColor.RED + "No Description set yet!");
        }
        chat.sendMessage(player, "");
        chat.sendMessage(player, "-------------------------------------------------");
        chat.sendActionMessage(player, new ClickData("                    §7[§3Back to Recipe Book Editor§7]", (wolfyUtilities, player1) -> api.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
    }

    public static void sendLoreManager(Player player) {
        var itemMeta = ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getItems().getItem().getItemMeta();
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        chat.sendMessages(player,
                miniM.deserialize("<grey>-------------------[<red>Remove Lore</red>]------------------</grey>"),
                Component.empty()
        );
        List<String> lore;
        if (itemMeta != null && itemMeta.hasLore()) {
            lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
            int i = 0;
            for (String line : lore) {
                int finalI = i;
                chat.sendMessage(player, miniM.deserialize("<grey>[<red>-</red>]</grey> ").clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                    lore.remove(finalI);
                    itemMeta.setLore(lore);
                    ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                    sendLoreManager(player1);
                })).append(BukkitComponentSerializer.legacy().deserialize(line)));
                i++;
            }
        } else {
            chat.sendMessage(player, Component.text("No Lore set yet!", NamedTextColor.RED, TextDecoration.BOLD));
        }
        chat.sendMessages(player,
                Component.empty(),
                Component.text("-------------------------------------------------"),
                miniM.deserialize("                        <grey>[<yellow><b>Back to ItemCreator</b></yellow>]</grey>")
                        .clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> api.getInventoryAPI().getGuiHandler(player1).openCluster()))
        );
    }

    public static <T> void sendListEditor(Player player, Function<T, Component> toComponent, BiFunction<String, String[], T> toListItem, Supplier<List<T>> listSupplier, Consumer<List<T>> applyChanges) {
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        chat.sendMessages(player, chat.translated("msg.chat_editor.list_edit.title"), Component.text("│", NamedTextColor.GRAY));
        List<T> list = listSupplier.get();
        if (!list.isEmpty()) {
            int i = 0;
            for (T listItem : list) {
                final int finalEntryIndex = i;
                TagResolver tagResolver = Placeholder.unparsed("list_entry", String.valueOf(finalEntryIndex + 1));
                chat.sendMessage(player, false, Component.text("│", NamedTextColor.GRAY));
                chat.sendMessage(player, false, Component.text("├─ ", NamedTextColor.GRAY)
                        //.append(chat.translated("msg.chat_editor.list_edit.entry.number", tagResolver))
                        .append(Component.text(""))
                        .append(chat.translated("msg.chat_editor.list_edit.entry.move_up", tagResolver).clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                            List<T> currentList = listSupplier.get();
                            int aboveIndex = finalEntryIndex - 1;
                            if (aboveIndex < 0) {
                                return; // Can't move first entry up
                            } else {
                                T below = currentList.get(aboveIndex);
                                currentList.set(aboveIndex, currentList.get(finalEntryIndex));
                                currentList.set(finalEntryIndex, below);
                            }
                            applyChanges.accept(currentList);
                            sendListEditor(player1, toComponent, toListItem, listSupplier, applyChanges);
                        })))
                        .append(Component.text(" "))
                        .append(chat.translated("msg.chat_editor.list_edit.entry.move_down", tagResolver).clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                            List<T> currentList = listSupplier.get();
                            int belowIndex = finalEntryIndex + 1;
                            if (belowIndex >= currentList.size()) {
                                return; // Can't move last entry down
                            } else {
                                T below = currentList.get(belowIndex);
                                currentList.set(belowIndex, currentList.get(finalEntryIndex));
                                currentList.set(finalEntryIndex, below);
                            }
                            applyChanges.accept(currentList);
                            sendListEditor(player1, toComponent, toListItem, listSupplier, applyChanges);
                        })))
                        .append(Component.text(" "))
                        .append(chat.translated("msg.chat_editor.list_edit.entry.add_below", tagResolver).clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                            chat.sendMessage(player1, chat.translated("msg.chat_editor.list_edit.input_new_entry"));
                            api.getInventoryAPI(CCCache.class).getGuiHandler(player1).setChatInputAction((guiHandler, player2, value, args) -> {
                                List<T> currentList = listSupplier.get();
                                if (finalEntryIndex + 1 >= currentList.size()) {
                                    currentList.add(toListItem.apply(value, args));
                                } else {
                                    currentList.add(finalEntryIndex + 1, toListItem.apply(value, args));
                                }
                                applyChanges.accept(currentList);
                                sendListEditor(player1, toComponent, toListItem, listSupplier, applyChanges);
                                return true;
                            });
                        })))
                        .append(Component.text(" "))
                        .append(chat.translated("msg.chat_editor.list_edit.entry.remove", tagResolver).clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                            list.remove(finalEntryIndex);
                            applyChanges.accept(list);
                            sendListEditor(player1, toComponent, toListItem, listSupplier, applyChanges);
                        })))
                        .append(Component.text(" "))
                        .append(chat.translated("msg.chat_editor.list_edit.entry.edit", tagResolver).clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                            chat.sendMessage(player1, chat.translated("msg.chat_editor.list_edit.input_new_entry", tagResolver));
                            api.getInventoryAPI(CCCache.class).getGuiHandler(player1).setChatInputAction((guiHandler, player2, value, args) -> {
                                List<T> currentList = listSupplier.get();
                                if (finalEntryIndex < currentList.size()) {
                                    currentList.set(finalEntryIndex, toListItem.apply(value, args));
                                    applyChanges.accept(currentList);
                                    sendListEditor(player1, toComponent, toListItem, listSupplier, applyChanges);
                                }
                                return true;
                            });
                        })))
                );
                chat.sendMessage(player, false, Component.text("╞", NamedTextColor.GRAY).append(Component.text("")
                        .append(miniM.deserialize(" <white><entry_val>", Placeholder.component("entry_val", toComponent.apply(listItem))))
                ));
                i++;
            }
        } else {
            chat.sendMessage(player, false, Component.text("╞═ ", NamedTextColor.GRAY).append(chat.translated("msg.chat_editor.list_edit.entry.add").clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                sendListEditor(player1, toComponent, toListItem, listSupplier, applyChanges);
                chat.sendMessage(player1, chat.translated("msg.chat_editor.list_edit.input_add_entry"));
                api.getInventoryAPI(CCCache.class).getGuiHandler(player1).setChatInputAction((guiHandler, player2, value, args) -> {
                    List<T> currentList = listSupplier.get();
                    currentList.add(toListItem.apply(value, args));
                    applyChanges.accept(currentList);
                    sendListEditor(player1, toComponent, toListItem, listSupplier, applyChanges);
                    return true;
                });
            }))));
        }
        chat.sendMessages(player,
                Component.text("│", NamedTextColor.GRAY),
                chat.translated("msg.chat_editor.list_edit.back_to_gui").clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> api.getInventoryAPI().getGuiHandler(player1).openCluster()))
        );
    }

    public static void sendLoreEditor(Player player) {
        sendListEditor(player, line -> BukkitComponentSerializer.legacy().deserialize(line), (value, s) -> BukkitComponentSerializer.legacy().serialize(miniM.deserialize(value)), () -> {
            var itemMeta = ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getItems().getItem().getItemMeta();
            return itemMeta != null && itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        }, lore -> {
            var itemMeta = ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getItems().getItem().getItemMeta();
            itemMeta.setLore(lore);
            ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
        });
    }

    public static void sendAttributeModifierManager(Player player) {
        CCCache cache = ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache());
        var items = cache.getItems();
        var itemMeta = items.getItem().getItemMeta();
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
                                cache1.getItems().getItem().setItemMeta(itemMeta);
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
    }

    public static void sendRecipeItemLoadingError(String prefix, String namespace, String key, Exception ex) {
        api.getConsole().warn(prefix + "[Error] Invalid Recipe: \"" + namespace + ":" + key + "\": " + (ex.getMessage() != null ? ex.getMessage() : ""));
        if (ex.getCause() != null) {
            api.getConsole().warn(prefix + "[Error]   Caused by: " + ex.getCause().getMessage());
        }
        if (CustomCrafting.inst().getConfigHandler().getConfig().isPrintingStacktrace()) {
            api.getConsole().warn("------------------[StackTrace]-------------------");
            ex.printStackTrace();
        }
    }
}
