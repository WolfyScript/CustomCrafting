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
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;

public class RecipeCreatorCraftingEliteSettings extends RecipeCreator {

    public static final String KEY = "elite_crafting_settings";

    public RecipeCreatorCraftingEliteSettings(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "elite_crafting_settings", 27, customCrafting);
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, ClusterMain.BACK);
        update.setButton(9, ClusterRecipeCreator.HIDDEN);
        update.setButton(11, ClusterRecipeCreator.GROUP);
        update.setButton(13, ClusterRecipeCreator.CONDITIONS);
        update.setButton(15, ClusterRecipeCreator.EXACT_META);
        update.setButton(17, ClusterRecipeCreator.PRIORITY);
    }

}
