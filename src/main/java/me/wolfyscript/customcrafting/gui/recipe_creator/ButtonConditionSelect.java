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
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;

class ButtonConditionSelect extends ActionButton<CCCache> {

    ButtonConditionSelect(NamespacedKey key) {
        super("icon_" + key.toString("_"), new ButtonState<>("select", Condition.getGuiComponent(key).getIcon(), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getConditionsCache().setSelectedCondition(key);
            return true;
        }, (values, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            var langAPI = guiHandler.getApi().getLanguageAPI();
            Condition.AbstractGUIComponent<?> guiComponent = Condition.getGuiComponent(key);
            if (guiComponent != null) {
                values.put("%name%", langAPI.replaceColoredKeys(guiComponent.getName()));
                values.put("%description%", langAPI.replaceColoredKeys(guiComponent.getDescription()));
            }
            return itemStack;
        }));
    }
}
