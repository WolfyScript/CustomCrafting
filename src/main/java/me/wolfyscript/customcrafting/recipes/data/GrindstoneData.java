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

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.recipes.CustomRecipeGrindstone;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GrindstoneData extends RecipeData<CustomRecipeGrindstone> {

    private final boolean validItem;

    public GrindstoneData(@NotNull CustomRecipeGrindstone recipe, boolean validItem, IngredientData inputTop, IngredientData inputBottom) {
        super(recipe, new IngredientData[]{inputTop, inputBottom});
        this.validItem = validItem;
    }

    public Optional<IngredientData> topIngredient() {
        return bySlot(0);
    }

    public Optional<IngredientData> bottomIngredient() {
        return bySlot(1);
    }

    public Optional<StackReference> inputTop() {
        return bySlot(0).map(IngredientData::reference);
    }

    public Optional<StackReference> inputBottom() {
        return bySlot(1).map(IngredientData::reference);
    }

    public boolean isValidItem() {
        return validItem;
    }

    @Deprecated(forRemoval = true, since = "4.16.9")
    public CustomItem getInputTop() {
        return getBySlot(0).customItem();
    }

    @Deprecated(forRemoval = true, since = "4.16.9")
    public CustomItem getInputBottom() {
        return getBySlot(1).customItem();
    }
}
