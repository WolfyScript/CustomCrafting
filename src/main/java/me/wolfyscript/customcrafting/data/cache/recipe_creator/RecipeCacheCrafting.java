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

package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CraftingRecipeShaped;
import me.wolfyscript.customcrafting.recipes.CraftingRecipeShapeless;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;

public class RecipeCacheCrafting extends RecipeCacheCraftingAbstract<AdvancedRecipeSettings> {

    RecipeCacheCrafting(CustomCrafting customCrafting) {
        super(customCrafting);
    }

    RecipeCacheCrafting(CustomCrafting customCrafting, CraftingRecipe<?, AdvancedRecipeSettings> recipe) {
        super(customCrafting, recipe);
    }

    @Override
    protected CraftingRecipe<?, AdvancedRecipeSettings> constructRecipe() {
        return create(shapeless ? new CraftingRecipeShapeless(key) : new CraftingRecipeShaped(key));
    }

}
