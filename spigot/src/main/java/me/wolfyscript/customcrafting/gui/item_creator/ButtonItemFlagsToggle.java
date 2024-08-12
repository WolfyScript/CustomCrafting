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
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

public class ButtonItemFlagsToggle extends ToggleButton<CCCache> {

    public ButtonItemFlagsToggle(String flagId, ItemFlag itemFlag, Material material) {
        super("flags." + flagId, (cache, guiHandler, player, guiInventory, i) -> {
            return cache.getItems().asBukkitIdentifier().map(identifier -> !ItemUtils.isAirOrNull(identifier.stack()) && identifier.stack().getItemMeta().hasItemFlag(itemFlag)).orElse(false);
        }, new ButtonState<>("flags." + flagId + ".enabled", material, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getItems().modifyOriginalStack(stack -> stack.removeItemFlags(itemFlag));
            return true;
        }), new ButtonState<>("flags." + flagId + ".disabled", material, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getItems().modifyOriginalStack(stack -> stack.addItemFlags(itemFlag));
            return true;
        }));
    }
}
