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

import com.wolfyscript.utilities.bukkit.world.items.reference.BukkitStackIdentifier;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

public class TabCustomModelData extends ItemCreatorTabVanilla {

    public static final String KEY = "custom_model_data";

    public TabCustomModelData() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.REDSTONE, this));
        creator.registerButton(new ChatInputButton<>("custom_model_data.set", Material.GREEN_CONCRETE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var items = guiHandler.getCustomCache().getItems();
            hashMap.put("%VAR%", (items.getItem().hasItemMeta() && items.getItem().getItemMeta().hasCustomModelData() ? items.getItem().getItemMeta().getCustomModelData() : "&7&l/") + "");
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            BukkitStackIdentifier identifier = guiHandler.getCustomCache().getItems().asBukkitIdentifier().orElse(null);
            if (identifier != null) {
                var itemMeta = identifier.stack().getItemMeta();
                if (!(itemMeta instanceof Repairable)) {
                    return true;
                }
                try {
                    int value = Integer.parseInt(s);
                    itemMeta.setCustomModelData(value);
                    identifier.stack().setItemMeta(itemMeta);
                    creator.sendMessage(player, "custom_model_data.success", new Pair<>("%VALUE%", String.valueOf(value)));
                } catch (NumberFormatException e) {
                    creator.sendMessage(player, "custom_model_data.invalid_value", new Pair<>("%VALUE%", s));
                    return true;
                }
            }
            return false;
        }));
        creator.registerButton(new ActionButton<>("custom_model_data.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            guiHandler.getCustomCache().getItems().asBukkitIdentifier().ifPresent(identifier -> {
                var itemMeta = identifier.stack().getItemMeta();
                itemMeta.setCustomModelData(null);
                identifier.stack().setItemMeta(itemMeta);
            });
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "custom_model_data.set");
        update.setButton(32, "custom_model_data.reset");
    }
}
