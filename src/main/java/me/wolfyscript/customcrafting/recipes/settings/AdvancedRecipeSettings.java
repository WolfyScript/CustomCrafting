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

package me.wolfyscript.customcrafting.recipes.settings;

import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;

public class AdvancedRecipeSettings implements CraftingRecipeSettings<AdvancedRecipeSettings> {

    @JsonIgnore
    private boolean allowVanillaRecipe;

    public AdvancedRecipeSettings() {
        this.allowVanillaRecipe = true;
    }

    public AdvancedRecipeSettings(AdvancedRecipeSettings settings) {
        this.allowVanillaRecipe = settings.allowVanillaRecipe;
    }

    /**
     * @deprecated Replaced by {@link ICustomVanillaRecipe#isVisibleVanillaBook()}
     */
    @JsonIgnore
    @Deprecated
    public boolean isAllowVanillaRecipe() {
        return allowVanillaRecipe;
    }

    /**
     * @deprecated Replaced by {@link ICustomVanillaRecipe#setVisibleVanillaBook(boolean)}
     */
    @JsonIgnore
    @Deprecated
    public void setAllowVanillaRecipe(boolean allowVanillaRecipe) {
        this.allowVanillaRecipe = allowVanillaRecipe;
    }

    @Override
    public AdvancedRecipeSettings clone() {
        return new AdvancedRecipeSettings(this);
    }
}
