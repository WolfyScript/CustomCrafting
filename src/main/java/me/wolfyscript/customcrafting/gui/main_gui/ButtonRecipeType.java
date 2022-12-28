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

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class ButtonRecipeType {

    static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, String key, RecipeType<?> recipeType, ItemStack icon) {
        buttonBuilder.action(key).state(state -> state.icon(icon).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().setRecipeType(recipeType);
            cache.setSetting(Setting.RECIPE_CREATOR);
            holder.getGuiHandler().openWindow(new BukkitNamespacedKey("recipe_creator", recipeType.getCreatorID()));
            return ButtonInteractionResult.cancel(true);
        })).register();
    }

    static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, String key, RecipeType<?> recipeType, Material icon) {
        register(buttonBuilder, key, recipeType, new ItemStack(icon));
    }
}
