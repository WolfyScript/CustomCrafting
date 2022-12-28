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
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonChatInput;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabLore extends ItemCreatorTabVanilla {

    public static final String KEY = "lore";

    public TabLore() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        var loreChatEditor = creator.getCustomCrafting().isPaper() ? ChatUtils.createPaperLoreChatEditor(creator.getInventoryAPI()) : ChatUtils.createLoreChatEditor(creator.getInventoryAPI());

        ButtonOption.register(creator.getButtonBuilder(), Material.WRITABLE_BOOK, this);
        creator.getButtonBuilder().chatInput(KEY + ".add").state(state -> state.icon(Material.WRITABLE_BOOK)).inputAction((guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getItems().getItem().addLoreLine(BukkitComponentSerializer.legacy().serialize(api.getChat().getMiniMessage().deserialize(s, Placeholder.component("emtpy", Component.empty()))));
            return false;
        }).register();
        creator.getButtonBuilder().action(KEY + ".edit").state(state -> state.icon(Material.WRITABLE_BOOK).action((holder, cache, btn, slot, details) -> {
            loreChatEditor.send(holder.getPlayer());
            holder.getGuiHandler().close();
            return ButtonInteractionResult.cancel(true);
        })).register();
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(31, KEY + ".edit");
    }
}
