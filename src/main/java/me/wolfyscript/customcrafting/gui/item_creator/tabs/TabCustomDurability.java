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
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabCustomDurability extends ItemCreatorTab {

    public static final String KEY = "custom_durability";

    public TabCustomDurability() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        ButtonOption.register(creator.getButtonBuilder(), Material.DIAMOND_SWORD, this);
        creator.registerButton(new ButtonAction<>("custom_durability.remove", Material.RED_CONCRETE_POWDER, (cache, guiHandler, player, inventory, btn, i, event) -> {
            var items = cache.getItems();
            items.getItem().removeCustomDurability();
            return true;
        }));
        creator.registerButton(new ButtonChatInput<>("custom_durability.set_durability", Material.GREEN_CONCRETE, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getCustomDurability());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            try {
                guiHandler.getCustomCache().getItems().getItem().setCustomDurability(Integer.parseInt(strings[0]));
            } catch (NumberFormatException ex) {
                return true;
            }
            guiHandler.openCluster();
            return false;
        }));
        creator.registerButton(new ButtonChatInput<>("custom_durability.set_damage", Material.RED_CONCRETE, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var items = guiHandler.getCustomCache().getItems();
            values.put("%VAR%", items.getItem().getCustomDamage());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            try {
                guiHandler.getCustomCache().getItems().getItem().setCustomDamage(Integer.parseInt(strings[0]));
            } catch (NumberFormatException ex) {
                return true;
            }
            guiHandler.openCluster();
            return false;
        }));
        creator.registerButton(new ButtonChatInput<>("custom_durability.set_tag", Material.NAME_TAG, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var items = guiHandler.getCustomCache().getItems();
            values.put("%VAR%", items.getItem().getCustomDurabilityTag());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            try {
                guiHandler.getCustomCache().getItems().getItem().setCustomDurabilityTag(s);
            } catch (NumberFormatException ex) {
                return true;
            }
            guiHandler.openCluster();
            return false;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(29, "custom_durability.set_damage");
        update.setButton(31, "custom_durability.set_tag");
        update.setButton(33, "custom_durability.set_durability");
        update.setButton(40, "custom_durability.remove");
    }
}
