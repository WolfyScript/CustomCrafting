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

package me.wolfyscript.customcrafting.recipes.data;

import me.wolfyscript.customcrafting.recipes.CustomRecipeSmithing;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

import java.util.Map;

public class SmithingData extends RecipeData<CustomRecipeSmithing> implements ISmithingData<CustomRecipeSmithing>{

    public SmithingData(CustomRecipeSmithing recipe, IngredientData[] ingredients) {
        super(recipe, ingredients);
    }

    @Override
    public CustomItem getTemplate() {
        return getBySlot(0) == null ? null : getBySlot(0).customItem();
    }

    public CustomItem getBase() {
        return getBySlot(1).customItem();
    }

    public CustomItem getAddition() {
        return getBySlot(2).customItem();
    }
}
