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

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import me.wolfyscript.customcrafting.data.CCCache;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

class ButtonTagContainer extends ButtonAction<CCCache> {

    ButtonTagContainer(NamespacedKey namespacedKey) {
        super("tag." + namespacedKey.toString("."), new ButtonState<>("tag_container", Material.NAME_TAG, (cache, guiHandler, player, guiInventory, slot, event) -> {
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT)) {
                var recipeItemStack = cache.getRecipeCreatorCache().getTagSettingsCache().getRecipeItemStack();
                if (recipeItemStack != null) {
                    recipeItemStack.getTags().remove(namespacedKey);
                }
            }
            return true;
        }, (values, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            values.put("%namespaced_key%", namespacedKey.toString());
            return itemStack;
        }));
    }
}
