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
import me.wolfyscript.customcrafting.recipes.CustomRecipeSmithing;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;

import java.util.Optional;

public class SmithingData extends RecipeData<CustomRecipeSmithing> implements ISmithingData<CustomRecipeSmithing>{

    private static final boolean IS_1_20 = ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0));

    public SmithingData(CustomRecipeSmithing recipe, IngredientData[] ingredients) {
        super(recipe, ingredients);
    }

    @Override
    public Optional<StackReference> template() {
        return bySlot(0).map(IngredientData::reference);
    }

    @Override
    public Optional<StackReference> base() {
        return bySlot(IS_1_20 ? 1 : 0).map(IngredientData::reference);
    }

    @Override
    public Optional<StackReference> addition() {
        return bySlot(IS_1_20 ? 2 : 1).map(IngredientData::reference);
    }

    @Deprecated
    @Override
    public CustomItem getTemplate() {
        return getBySlot(0) == null ? null : getBySlot(0).customItem();
    }

    @Deprecated
    public CustomItem getBase() {
        return getBySlot(IS_1_20 ? 1 : 0).customItem();
    }

    @Deprecated
    public CustomItem getAddition() {
        return getBySlot(IS_1_20 ? 2 : 1).customItem();
    }
}
