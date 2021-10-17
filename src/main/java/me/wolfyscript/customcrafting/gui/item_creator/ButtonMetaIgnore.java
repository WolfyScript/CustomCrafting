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

package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.Meta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.MetaSettings;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

import java.util.List;

public class ButtonMetaIgnore extends ActionButton<CCCache> {

    public ButtonMetaIgnore(NamespacedKey metaKey) {
        super("meta_ignore." + metaKey.toString("."), new ButtonState<>("meta_ignore", Material.CYAN_CONCRETE, (cache, guiHandler, player, guiInventory, slot, inventoryInteractEvent) -> {
            Meta meta = guiHandler.getCustomCache().getItems().getItem().getMetaSettings().get(metaKey);
            List<MetaSettings.Option> options = meta.getAvailableOptions();
            int i = options.indexOf(meta.getOption()) + 1;
            if (i >= options.size()) {
                i = 0;
            }
            meta.setOption(options.get(i));
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getMetaSettings().get(metaKey).getOption().toString());
            return itemStack;
        }));
    }
}
