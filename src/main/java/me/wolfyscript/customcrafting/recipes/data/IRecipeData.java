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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.items.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IRecipeData<R extends CustomRecipe<?>> {

    R getRecipe();

    Result getResult();

    void setResult(@NotNull Result result);

    /**
     * The slots indexed by the data <b>Matrix Slot (CraftingInventory slots)</b> and not the recipe slot!
     *
     * @return The map with Matrix Slot keys and data values.
     * @deprecated Use {@link #getNonNullIngredients()} and use {@link IngredientData#matrixSlot()}
     */
    @Deprecated
    Map<Integer, IngredientData> getIndexedBySlot();

    /**
     * Returns a stream consisting of the non-null IngredientData.
     *
     * @return A stream of non-null IngredientData.
     */
    Stream<IngredientData> getNonNullIngredients();

    /**
     * The slots indicate the index (position) of the Ingredient inside the recipe.
     * For normal recipes that means from 0 to 9.
     * For Elite recipes the range is from 0 to 36.
     * <p>
     * For the correct position open the in-game Recipe Creator GUI and see in which slot the ingredient is.
     * You may take the character saved in the config and use the index of it inside this String "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
     *
     * @param slot The recipe slot to get the {@link IngredientData} for.
     * @return The {@link IngredientData} of the specified recipe slot.
     */
    @Nullable IngredientData getBySlot(int slot);

    /**
     * The slots indicate the index (position) of the Ingredient inside the recipe.
     * For normal recipes that means from 0 to 9.
     * For Elite recipes the range is from 0 to 36.
     * <p>
     * For the correct position open the in-game Recipe Creator GUI and see in which slot the ingredient is.
     * You may take the character saved in the config and use the index of it inside this String "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
     *
     * @param slots The recipe slots to get the {@link IngredientData} for.
     * @return A list of {@link IngredientData} of the specified recipe slots.
     */
    List<IngredientData> getBySlots(int[] slots);

}
