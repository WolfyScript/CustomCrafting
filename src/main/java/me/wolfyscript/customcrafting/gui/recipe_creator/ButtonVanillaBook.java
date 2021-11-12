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

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

class ButtonVanillaBook extends ToggleButton<CCCache> {

    ButtonVanillaBook() {
        super(ClusterRecipeCreator.VANILLA_BOOK.getKey(), (cache, guiHandler, player, guiInventory, i) -> cache.getRecipeCreatorCache().getRecipeCache().isVanillaBook(), new ButtonState<>(ClusterRecipeCreator.VANILLA_BOOK_ENABLED, Material.GRASS_BLOCK, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getRecipeCache().setVanillaBook(false);
            return true;
        }), new ButtonState<>(ClusterRecipeCreator.VANILLA_BOOK_DISABLED, Material.GRASS_BLOCK, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getRecipeCache().setVanillaBook(true);
            return true;
        }));
    }
}
