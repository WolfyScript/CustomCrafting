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

package me.wolfyscript.customcrafting.gui.recipe_view;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import org.bukkit.Bukkit;

import java.util.Optional;

public class MenuSingleRecipe extends CCWindow {

    public static final String KEY = "single_recipe";

    MenuSingleRecipe(GuiCluster<CCCache> guiCluster, CustomCrafting customCrafting) {
        super(guiCluster, KEY, 54, customCrafting);
        setForceSyncUpdate(true);
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        Optional<CustomRecipe<?>> recipeOptional = update.getGuiHandler().getCustomCache().getCacheRecipeView().getRecipe();
        if (recipeOptional.isPresent()) {
            CustomRecipe<?> customRecipe = recipeOptional.get();
            customRecipe.renderMenu(this, update);
        } else {
            // If there is no recipe available, why is the menu open?
            Bukkit.getScheduler().runTask(customCrafting, () -> update.getGuiHandler().close());
        }
    }
}
