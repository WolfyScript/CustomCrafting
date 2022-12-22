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
import com.wolfyscript.utilities.bukkit.chat.ChatColor;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonChatInput;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabLocalizedName extends ItemCreatorTabVanilla {

    public static final String KEY = "localized_name";

    public TabLocalizedName() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        ButtonOption.register(creator.getButtonBuilder(), Material.NAME_TAG, this);
        creator.getButtonBuilder().chatInput("localized_name.set").state(state -> state.icon(Material.NAME_TAG).render((cache, guiHandler, player, inventory, btn, itemStack, i) -> {
            return CallbackButtonRender.UpdateResult.of(Placeholder.parsed("var", guiHandler.getCustomCache().getItems().getItem().getItemMeta().getLocalizedName()));
        })).inputAction((guiHandler, player, s, strings) -> {
            var itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            itemMeta.setLocalizedName(ChatColor.convert(s));
            guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
            return false;
        }).register();
        creator.getButtonBuilder().action("localized_name.remove").state(state -> state.icon(Material.NAME_TAG).action((cache, guiHandler, player, inventory, btn, i, inventoryClickEvent) -> {
            var itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            itemMeta.setLocalizedName(null);
            guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
            return true;
        })).register();
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "localized_name.set");
        update.setButton(32, "localized_name.remove");
    }
}
