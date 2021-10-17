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

import me.wolfyscript.customcrafting.recipes.CustomRecipeGrindstone;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

import java.util.Optional;

public class GrindstoneData {

    private final CustomRecipeGrindstone recipe;
    private final Result result;
    private final CustomItem inputTop;
    private final CustomItem inputBottom;
    private final boolean validItem;

    public GrindstoneData(CustomRecipeGrindstone recipe, Result result, boolean validItem, CustomItem inputTop, CustomItem inputBottom) {
        this.recipe = recipe;
        this.result = result;
        this.validItem = validItem;
        this.inputTop = inputTop;
        this.inputBottom = inputBottom;
    }

    public CustomItem getInputBottom() {
        return inputBottom;
    }

    public CustomItem getInputTop() {
        return inputTop;
    }

    public CustomRecipeGrindstone getRecipe() {
        return recipe;
    }

    public Optional<Result> getResult() {
        return Optional.ofNullable(result);
    }

    public boolean isValidItem() {
        return validItem;
    }
}
