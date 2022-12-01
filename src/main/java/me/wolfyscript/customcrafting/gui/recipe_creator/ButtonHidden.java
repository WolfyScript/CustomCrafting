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

import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonToggle;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import me.wolfyscript.customcrafting.data.CCCache;

class ButtonHidden extends ButtonToggle<CCCache> {

    ButtonHidden() {
        super(ClusterRecipeCreator.HIDDEN.getKey(), (cache, guiHandler, player, guiInventory, i) -> cache.getRecipeCreatorCache().getRecipeCache().isHidden(),
                new ButtonState<>(ClusterRecipeCreator.HIDDEN_ENABLED, PlayerHeadUtils.getViaURL("85e5bf255d5d7e521474318050ad304ab95b01a4af0bae15e5cd9c1993abcc98"), (cache, guiHandler, player, inventory, slot, event) -> {
                    cache.getRecipeCreatorCache().getRecipeCache().setHidden(false);
                    return true;
                }), new ButtonState<>(ClusterRecipeCreator.HIDDEN_DISABLED, PlayerHeadUtils.getViaURL("ce9d49dd09ecee2a4996965514d6d301bf12870c688acb5999b6658e1dfdff85"), (cache, guiHandler, player, inventory, slot, event) -> {
                    cache.getRecipeCreatorCache().getRecipeCache().setHidden(true);
                    return true;
                }));
    }
}
