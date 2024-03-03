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
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabUnbreakable extends ItemCreatorTabVanilla {

    public static final String KEY = "unbreakable";

    public TabUnbreakable() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ToggleButton<>(KEY, (cache, guiHandler, player, guiInventory, i) -> {
            return cache.getItems().asBukkitIdentifier().map(identifier -> !ItemUtils.isAirOrNull(identifier.stack()) && identifier.stack().getItemMeta().isUnbreakable()).orElse(false);
        }, new ButtonState<>(KEY + ".enabled", Material.BEDROCK, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            cache.getItems().asBukkitIdentifier().ifPresent(identifier -> {
                var itemMeta = identifier.stack().getItemMeta();
                itemMeta.setUnbreakable(false);
                identifier.stack().setItemMeta(itemMeta);
            });
            return true;
        }), new ButtonState<>(KEY + ".disabled", Material.GLASS, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            cache.getItems().asBukkitIdentifier().ifPresent(identifier -> {
                var itemMeta = identifier.stack().getItemMeta();
                itemMeta.setUnbreakable(true);
                identifier.stack().setItemMeta(itemMeta);
            });
            return true;
        })));
    }

    @Override
    public String getOptionButton() {
        return KEY;
    }

    @Override
    public boolean shouldRender(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        return super.shouldRender(update, cache, items, customItem, item) && !ItemUtils.isAirOrNull(item);
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, KEY + ".set");
        update.setButton(32, KEY + ".remove");
    }
}
