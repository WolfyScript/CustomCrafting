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

import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackChatTabComplete;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackChatTabComplete;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.InventoryAPI;
import org.bukkit.entity.Player;

public class CollectionEditor<C extends CustomCache, T> {

    protected final BukkitChat chat;
    protected final InventoryAPI<C> invAPI;

    private int entriesPerPage = 7;

    protected final SupplyEntryCollection<T, C> listSupplier;
    protected final ParseEntryToComponent<T, C> toComponent;
    protected final ParseChatInputToEntry<T, C> toListEntry;
    protected AddEntry<T, C> addEntry;
    protected EditEntry<T, C> editEntry;
    protected RemoveEntry<T, C> removeEntry;
    protected MoveEntry<C> moveEntry;
    protected CallbackChatTabComplete<C> tabComplete;
    protected SendInputInfoMessages<C> sendInputInfoMessages;

    public CollectionEditor(InventoryAPI<C> invAPI, SupplyEntryCollection<T, C> listSupplier, ParseEntryToComponent<T, C> toComponent, ParseChatInputToEntry<T, C> toListEntry) {
        this.invAPI = invAPI;
        this.listSupplier = listSupplier;
        this.toComponent = toComponent;
        this.toListEntry = toListEntry;
        this.chat = invAPI.getWolfyUtils().getChat();
        this.addEntry = null;
        this.editEntry = null;
        this.removeEntry = null;
        this.moveEntry = null;
        this.sendInputInfoMessages = (guiHandler, player, cache) -> chat.sendMessage(player, chat.translated("msg.input.wui_command"));
    }

    public CollectionEditor<C, T> setEntriesPerPage(int entriesPerPage) {
        this.entriesPerPage = entriesPerPage;
        return this;
    }

    public CollectionEditor<C, T> setTabComplete(ChatTabComplete<C> tabComplete) {
        this.tabComplete = tabComplete;
        return this;
    }

    public CollectionEditor<C, T> onAdd(AddEntry<T, C> addEntry) {
        this.addEntry = addEntry;
        return this;
    }

    public CollectionEditor<C, T> onEdit(EditEntry<T, C> editEntry) {
        this.editEntry = editEntry;
        return this;
    }

    public CollectionEditor<C, T> onMove(MoveEntry<C> moveEntry) {
        this.moveEntry = moveEntry;
        return this;
    }

    public CollectionEditor<C, T> onRemove(RemoveEntry<T, C> removeEntry) {
        this.removeEntry = removeEntry;
        return this;
    }

    public CollectionEditor<C, T> setSendInputInfoMessages(SendInputInfoMessages<C> sendInputInfoMessages) {
        this.sendInputInfoMessages = sendInputInfoMessages;
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
        _sendTitle(player);
        GuiHandler<C> guiHandler = invAPI.getGuiHandler(player);
        Collection<T> list = listSupplier.get(guiHandler, player, guiHandler.getCustomCache());
        int maxPages = list.size() / entriesPerPage + (list.size() % entriesPerPage > 0 ? 1 : 0);
        if (list.isEmpty()) {
            _sendEmpty(guiHandler, player);
        } else {
            int startPoint = page * entriesPerPage;
            list = list.stream().skip(startPoint).limit(entriesPerPage).toList();
            _sendEntries(player, page, startPoint, list);
        }
        _sendPageButtons(player, maxPages, page);
        _sendBackToGui(player);
    }

    protected void _sendPageButtons(Player player, int maxPages, int page) {
        chat.sendMessage(player, false, Component.text("│", NamedTextColor.GRAY));
        chat.sendMessage(player, false, Component.text("├ ", NamedTextColor.GRAY)
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
                    GuiHandler<C> guiHandler = invAPI.getGuiHandler(player);
                    Collection<T> currentList = listSupplier.get(guiHandler, player1, guiHandler.getCustomCache());
                    int pages = (currentList.size() / entriesPerPage) + (currentList.size() % entriesPerPage > 0 ? 1 : 0);
                    if (page + 1 < pages) {
                        send(player1, page + 1);
                    } else {
                        send(player1, page);
                    }
                })))
        );
    }


    protected void _sendTitle(Player player) {
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        chat.sendMessages(player, chat.translated("msg.chat_editor.list_edit.title"), Component.text("│", NamedTextColor.GRAY));
    }

    protected void _sendNewEntryPrompt(GuiHandler<C> globalGuiHandler, Player player, String promptMsg, boolean edit, T previousEntry, int index, TagResolver... tagResolvers) {
        if (tagResolvers == null || tagResolvers.length == 0) {
            chat.sendMessage(player, chat.translated(promptMsg));
        } else {
            chat.sendMessage(player, chat.translated(promptMsg, tagResolvers));
        }
        sendInputInfoMessages.send(globalGuiHandler, player, globalGuiHandler.getCustomCache());
        invAPI.getGuiHandler(player).setChatInput((guiHandler, player2, value, args) -> {
            T listEntry = toListEntry.parse(guiHandler, player2, guiHandler.getCustomCache(), value, args);
            if (listEntry != null) {
                if (edit) {
                    editEntry.apply(guiHandler, player, guiHandler.getCustomCache(), index, previousEntry, listEntry);
                } else {
                    addEntry.apply(guiHandler, player, guiHandler.getCustomCache(), index, listEntry);
                }
                send(player2);
            }
            return true;
        }, tabComplete);
    }

    protected void _sendEmpty(GuiHandler<C> globalGuiHandler, Player player) {
        chat.sendMessage(player, false, Component.text("╞═ ", NamedTextColor.GRAY)
                .append(Component.text("")
                        .append(chat.translated("msg.chat_editor.list_edit.entry.add")
                                .clickEvent(chat.executable(player, true, (wolfyUtilities, player1) -> _sendNewEntryPrompt(globalGuiHandler, player1, "msg.chat_editor.list_edit.input_add_entry", false, null, -1, (TagResolver[]) null)))
                        ).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/wui "))
                ));
    }

    protected void _sendBackToGui(Player player) {
        chat.sendMessages(player, Component.text("│", NamedTextColor.GRAY), chat.translated("msg.chat_editor.list_edit.back_to_gui").clickEvent(chat.executable(player, true, (wolfyUtils, player1) -> invAPI.getGuiHandler(player1).openCluster())));
    }

    protected void _sendEntries(Player player, int page, int indexOffset, Collection<T> list) {
        int i = indexOffset;
        for (T listItem : list) {
            final int finalEntryIndex = i;
            GuiHandler<C> globalGuiHandler = invAPI.getGuiHandler(player);
            TagResolver tagResolver = Placeholder.unparsed("list_entry", String.valueOf(finalEntryIndex + 1));
            chat.sendMessage(player, false, Component.text("│", NamedTextColor.GRAY));

            Component modifiers = Component.text("╞", NamedTextColor.GRAY);
            if (moveEntry != null) {
                modifiers = modifiers.append(chat.translated("msg.chat_editor.list_edit.entry.move_up", tagResolver).clickEvent(chat.executable(player, true, (wolfyUtils, player1) -> {
                            GuiHandler<C> guiHandler = invAPI.getGuiHandler(player1);
                            int aboveIndex = finalEntryIndex - 1;
                            if (aboveIndex < 0) {
                                return; // Can't move first entry up
                            } else {
                                moveEntry.apply(guiHandler, player, guiHandler.getCustomCache(), finalEntryIndex, aboveIndex);
                            }
                            send(player1, page);
                        })))
                        .append(Component.text(" "))
                        .append(chat.translated("msg.chat_editor.list_edit.entry.move_down", tagResolver).clickEvent(chat.executable(player, true, (wolfyUtils, player1) -> {
                            GuiHandler<C> guiHandler = invAPI.getGuiHandler(player1);
                            int belowIndex = finalEntryIndex + 1;
                            if (belowIndex >= listSupplier.get(guiHandler, player1, guiHandler.getCustomCache()).size()) {
                                return; // Can't move last entry down
                            } else {
                                moveEntry.apply(guiHandler, player, guiHandler.getCustomCache(), finalEntryIndex, belowIndex);
                            }
                            send(player1, page);
                        })))
                        .append(Component.text(" "));
            }
            if (addEntry != null) {
                modifiers = modifiers.append(chat.translated("msg.chat_editor.list_edit.entry.add_below", tagResolver).clickEvent(chat.executable(player, true, (wolfyUtils, player1) -> _sendNewEntryPrompt(globalGuiHandler, player1, "msg.chat_editor.list_edit.input_new_entry", false, null, finalEntryIndex + 1, tagResolver))))
                        .append(Component.text(" "));
            }
            if (removeEntry != null) {
                modifiers = modifiers.append(chat.translated("msg.chat_editor.list_edit.entry.remove", tagResolver).clickEvent(chat.executable(player, true, (wolfyUtils, player1) -> {
                            GuiHandler<C> guiHandler = invAPI.getGuiHandler(player);
                            removeEntry.apply(guiHandler, player1, guiHandler.getCustomCache(), finalEntryIndex, listItem);
                            send(player1, page);
                        })))
                        .append(Component.text(" "));
            }
            if (editEntry != null) {
                modifiers = modifiers.append(chat.translated("msg.chat_editor.list_edit.entry.edit", tagResolver).clickEvent(chat.executable(player, true, (wolfyUtils, player1) -> _sendNewEntryPrompt(globalGuiHandler, player1, "msg.chat_editor.list_edit.input_new_entry", true, listItem, finalEntryIndex, tagResolver))));
            }
            chat.sendMessage(player, false, modifiers.append(Component.text("═")).append(chat.getMiniMessage().deserialize(" <white><entry_val>", Placeholder.component("entry_val", toComponent.parse(globalGuiHandler, player, globalGuiHandler.getCustomCache(), listItem)))));
            i++;
        }
    }

    public interface SendInputInfoMessages<C extends CustomCache> {

        void send(GuiHandler<C> guiHandler, Player player, C cache);

    }

    public interface SupplyEntryCollection<T, C extends CustomCache> {

        Collection<T> get(GuiHandler<C> guiHandler, Player player, C cache);

    }

    public interface ParseChatInputToEntry<T, C extends CustomCache> {

        T parse(GuiHandler<C> guiHandler, Player player, C cache, String msg, String[] args);

    }

    public interface ParseEntryToComponent<T, C extends CustomCache> {

        Component parse(GuiHandler<C> guiHandler, Player player, C cache, T element);

    }

    public interface MoveEntry<C extends CustomCache> {

        void apply(GuiHandler<C> guiHandler, Player player, C cache, int fromIndex, int toIndex);

    }

    public interface AddEntry<T, C extends CustomCache> {

        void apply(GuiHandler<C> guiHandler, Player player, C cache, int index, T entry);

    }

    public interface RemoveEntry<T, C extends CustomCache> {

        void apply(GuiHandler<C> guiHandler, Player player, C cache, int index, T entry);

    }

    public interface EditEntry<T, C extends CustomCache> {

        void apply(GuiHandler<C> guiHandler, Player player, C cache, int index, T previousEntry, T newEntry);

    }
}
