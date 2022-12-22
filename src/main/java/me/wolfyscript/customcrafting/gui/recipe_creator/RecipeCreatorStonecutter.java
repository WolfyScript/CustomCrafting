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

import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;

public class RecipeCreatorStonecutter extends RecipeCreator {

    public RecipeCreatorStonecutter(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "stonecutter", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        ButtonRecipeIngredient.register(getButtonBuilder(), 0);
        ButtonRecipeResult.register(getButtonBuilder());
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, BACK);
        update.setButton(4, ClusterRecipeCreator.HIDDEN);
        update.setButton(20, "recipe.ingredient_0");
        update.setButton(24, "recipe.result");

        update.setButton(42, ClusterRecipeCreator.GROUP);
        if (update.getGuiHandler().getCustomCache().getRecipeCreatorCache().getStonecuttingCache().isSaved()) {
            update.setButton(43, ClusterRecipeCreator.SAVE);
        }
        update.setButton(44, ClusterRecipeCreator.SAVE_AS);
    }

}
