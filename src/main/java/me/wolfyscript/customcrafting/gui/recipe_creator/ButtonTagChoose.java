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

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import me.wolfyscript.customcrafting.data.CCCache;
import org.bukkit.Material;
import org.bukkit.Tag;

class ButtonTagChoose extends ButtonAction<CCCache> {

    ButtonTagChoose(Tag<Material> tag) {
        super("tag." + BukkitNamespacedKey.fromBukkit(tag.getKey()).toString("."), new ButtonState<>("tag", Material.NAME_TAG, (cache, guiHandler, player, guiInventory, slot, event) -> {
            var recipeItemStack = cache.getRecipeCreatorCache().getTagSettingsCache().getRecipeItemStack();
            if (recipeItemStack != null) {
                recipeItemStack.getTags().add(BukkitNamespacedKey.fromBukkit(tag.getKey()));
            }
            guiHandler.openPreviousWindow();
            return true;
        }, (values, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            values.put("%namespaced_key%", BukkitNamespacedKey.fromBukkit(tag.getKey()).toString());
            itemStack.setType(tag.getValues().stream().findFirst().orElse(Material.NAME_TAG));
            return itemStack;
        }));
    }
}
