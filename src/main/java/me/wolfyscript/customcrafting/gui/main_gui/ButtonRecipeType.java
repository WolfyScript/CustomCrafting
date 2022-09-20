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

package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.utilities.api.inventory.gui.GuiMenuComponent;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class ButtonRecipeType extends ActionButton<CCCache> {

    ButtonRecipeType(String key, RecipeType<?> recipeType, ItemStack icon) {
        super(key, icon, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().setRecipeType(recipeType);
            cache.setSetting(Setting.RECIPE_CREATOR);
            guiHandler.openWindow(new NamespacedKey("recipe_creator", recipeType.getCreatorID()));
            return true;
        });
    }

    ButtonRecipeType(String key, RecipeType<?> recipeType, Material icon) {
        this(key, recipeType, new ItemStack(icon));
    }
}
