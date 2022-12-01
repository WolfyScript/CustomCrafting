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

package me.wolfyscript.customcrafting.data.cache.items;

import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonAction;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.GUIInventory;
import java.io.IOException;
import me.wolfyscript.customcrafting.data.CCCache;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;

public interface ItemsButtonAction extends CallbackButtonAction<CCCache> {

    @Override
    default boolean run(CCCache cache, GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int i, InventoryInteractEvent inventoryClickEvent) throws IOException {
        return execute(cache, cache.getItems(), guiHandler, player, inventory, i, inventoryClickEvent);
    }

    @Override
    default boolean run(CCCache cache, GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Button<CCCache> button, int i, InventoryInteractEvent inventoryInteractEvent) throws IOException;

    boolean execute(CCCache cache, Items items, GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int i, InventoryInteractEvent inventoryClickEvent);
}
