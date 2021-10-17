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

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.chat.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabLocalizedName extends ItemCreatorTabVanilla {

    public static final String KEY = "localized_name";

    public TabLocalizedName() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.NAME_TAG, this));
        creator.registerButton(new ChatInputButton<>("localized_name.set", Material.NAME_TAG, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getItemMeta().getLocalizedName());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            var itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            itemMeta.setLocalizedName(ChatColor.convert(s));
            guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
            return false;
        }));
        creator.registerButton(new ActionButton<>("localized_name.remove", Material.NAME_TAG, (cache, guiHandler, player, inventory, i, inventoryClickEvent) -> {
            var itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            itemMeta.setLocalizedName(null);
            guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "localized_name.set");
        update.setButton(32, "localized_name.remove");
    }
}
