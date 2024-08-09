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
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

public class ButtonFurnaceFuelToggle extends ToggleButton<CCCache> {

    public ButtonFurnaceFuelToggle(String id, Material material) {
        super("fuel." + id, (cache, guiHandler, player, guiInventory, i) -> cache.getItems().getItem().getFuelSettings().getAllowedBlocks().contains(material), new ButtonState<>("fuel." + id + ".enabled", material, (ItemsButtonAction) (testCache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().getFuelSettings().getAllowedBlocks().remove(material);
            return true;
        }), new ButtonState<>("fuel." + id + ".disabled", material, (ItemsButtonAction) (testCache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().getFuelSettings().getAllowedBlocks().add(material);
            return true;
        }));
    }
}
