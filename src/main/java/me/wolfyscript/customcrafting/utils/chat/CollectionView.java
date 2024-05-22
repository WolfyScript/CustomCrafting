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

package me.wolfyscript.customcrafting.utils.chat;

import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.format.NamedTextColor;
import me.wolfyscript.lib.net.kyori.adventure.text.format.TextDecoration;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import me.wolfyscript.utilities.api.chat.Chat;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.function.BiConsumer;

public class CollectionView<T> {

    protected final Chat chat;

    private int entriesPerPage = 7;
    private boolean prefix = false;

    protected final SupplyEntryCollection<T> listSupplier;
    protected final ParseEntryToComponent<T> toComponent;
    protected BiConsumer<Chat, Player> sendHeader = (chat1, player) -> {
    };
    protected BiConsumer<Chat, Player> sendFooter = (chat1, player) -> {
    };

    public CollectionView(Chat chat, SupplyEntryCollection<T> listSupplier, ParseEntryToComponent<T> toComponent) {
        this.listSupplier = listSupplier;
        this.toComponent = toComponent;
        this.chat = chat;
    }

    public CollectionView<T> prefix(boolean prefix) {
        this.prefix = prefix;
        return this;
    }

    public CollectionView<T> header(BiConsumer<Chat, Player> sendHeader) {
        this.sendHeader = sendHeader;
        return this;
    }

    public CollectionView<T> footer(BiConsumer<Chat, Player> sendFooter) {
        this.sendFooter = sendFooter;
        return this;
    }

    public CollectionView<T> setEntriesPerPage(int entriesPerPage) {
        this.entriesPerPage = entriesPerPage;
        return this;
    }

    /**
     * Sends the default state of the chat editor to the specified player.
     *
     * @param player The player to send the editor to.
     */
    public void send(Player player) {
        send(player, 0);
    }

    /**
     * Sends the chat editor at the specified page.
     *
     * @param player The player to open the editor for.
     * @param page   The page to open.
     */
    public void send(Player player, int page) {
        sendHeader(player);
        if (listSupplier != null) {
            Collection<T> list = listSupplier.get(player);
            if (!list.isEmpty()) {
                int maxPages = list.size() / entriesPerPage + (list.size() % entriesPerPage > 0 ? 1 : 0);
                int startPoint = page * entriesPerPage;
                list = list.stream().skip(startPoint).limit(entriesPerPage).toList();
                _sendEntries(player, page, startPoint, list);
                _sendPageButtons(player, maxPages, page);
            }
        }
        sendFooter.accept(chat, player);
    }

    protected void _sendPageButtons(Player player, int maxPages, int page) {
        chat.sendMessage(player, prefix, Component.text(" ", NamedTextColor.GRAY));
        chat.sendMessage(player, prefix, Component.text("  [ ", NamedTextColor.GRAY)
                .append(Component.text("« ", NamedTextColor.YELLOW, TextDecoration.BOLD).clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                    if (page - 1 >= 0) {
                        send(player1, page - 1);
                    } else {
                        send(player1, page);
                    }
                })))
                .append(chat.translated("msg.chat_editor.list_edit.pages",
                        Placeholder.unparsed("current_page", String.valueOf(page + 1)),
                        // Add 1 to the actual page to not show the page 0
                        Placeholder.unparsed("pages", String.valueOf(maxPages))))
                .append(Component.text(" »", NamedTextColor.YELLOW, TextDecoration.BOLD).clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> {
                    Collection<T> currentList = listSupplier.get(player1);
                    int pages = (currentList.size() / entriesPerPage) + (currentList.size() % entriesPerPage > 0 ? 1 : 0);
                    if (page + 1 < pages) {
                        send(player1, page + 1);
                    } else {
                        send(player1, page);
                    }
                })))
                .append(Component.text(" ]"))
        );
    }

    protected void sendHeader(Player player) {
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        sendHeader.accept(chat, player);
    }

    protected void _sendEntries(Player player, int page, int indexOffset, Collection<T> list) {
        int i = indexOffset;
        for (T listItem : list) {
            final int finalEntryIndex = i;
            TagResolver tagResolver = Placeholder.unparsed("list_entry", String.valueOf(finalEntryIndex + 1));
//            chat.sendMessage(player, prefix, Component.text("│", NamedTextColor.GRAY));
            Component modifiers = Component.text("  🛈 ", NamedTextColor.DARK_AQUA);
            chat.sendMessage(player, prefix, modifiers.append(toComponent.parse(player, listItem).colorIfAbsent(NamedTextColor.WHITE)));
            i++;
        }
    }

    public interface SupplyEntryCollection<T> {

        Collection<T> get(Player player);

    }

    public interface ParseEntryToComponent<T> {

        Component parse(Player player, T element);

    }

}
