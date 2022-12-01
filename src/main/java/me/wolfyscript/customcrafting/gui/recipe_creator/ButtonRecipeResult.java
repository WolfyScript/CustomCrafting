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

package me.wolfyscript.customcrafting.gui.recipe_creator;

import com.wolfyscript.utilities.bukkit.gui.button.ButtonItemInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.items.Result;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

class ButtonRecipeResult extends ButtonItemInput<CCCache> {

    ButtonRecipeResult() {
        super("recipe.result", new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            Result result = cache.getRecipeCreatorCache().getRecipeCache().getResult();
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getSlot() == slot && clickEvent.isRightClick() && clickEvent.isShiftClick()) {
                //Since shift clicking now updates all the available ItemInputButtons we only use the first button that was clicked.
                guiHandler.openWindow("result");
                return true;
            }
            return result.getItems().isEmpty() && !result.getTags().isEmpty();
        }, (cache, guiHandler, player, inventory, itemStack, i, event) -> {
            Result result = cache.getRecipeCreatorCache().getRecipeCache().getResult();
            if ((result.getItems().isEmpty() && !result.getTags().isEmpty()) || event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT) && event.getView().getTopInventory().equals(clickEvent.getClickedInventory())) {
                return;
            }
            result.put(0, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : null);
            result.buildChoices();
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            Result result = cache.getRecipeCreatorCache().getRecipeCache().getResult();
            return result != null ? result.getItemStack() : new ItemStack(Material.AIR);
        }));
    }
}
