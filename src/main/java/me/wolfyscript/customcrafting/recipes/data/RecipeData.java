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

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.recipes.items.target.MergeOption;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This object contains data of pre-crafted recipes like the recipe, ingredients and their slot ({@link IngredientData}), and the {@link Result}.
 * <p>
 * It indexes at which place of the inventory which CustomItem is used, so it can use the CustomItem consume options, and other options a user might have saved in the item.
 * <br>
 * The indexed Ingredients are used to target specific items, which are then used inside the {@link MergeOption}s.
 * </p>
 * <br>
 * Depending on the type of the recipe they might be:
 * <ul>
 *     <li>{@link CraftingData}</li>
 *     <li>{@link CookingRecipeData}
 *     <ul>
 *         <li>{@link FurnaceRecipeData}</li>
 *         <li>{@link BlastingRecipeData}</li>
 *         <li>{@link SmokerRecipeData}</li>
 *     </ul>
 *     </li>
 *     <li>{@link SmithingData}</li>
 *     <li>{@link AnvilData}</li>
 * </ul>
 *
 * @param <R> The type of the Recipe which this data stores.
 */
public abstract class RecipeData<R extends CustomRecipe<?>> {

    protected final R recipe;
    protected final IngredientData[] indexedBySlot;
    protected Result result;

    protected RecipeData(R recipe, IngredientData[] indexedBySlot) {
        this.result = recipe.getResult();
        this.recipe = recipe;
        this.indexedBySlot = indexedBySlot;
    }

    public R getRecipe() {
        return recipe;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * The slots indexed by the data <b>Matrix Slot (CraftingInventory slots)</b> and not the recipe slot!
     *
     * @return The map with Matrix Slot keys and data values.
     * @deprecated Use {@link #getNonNullIngredients()} and use {@link IngredientData#matrixSlot()}
     */
    @Deprecated
    public Map<Integer, IngredientData> getIndexedBySlot() {
        return getNonNullIngredients().collect(Collectors.toMap(IngredientData::matrixSlot, Function.identity()));
    }

    /**
     * Returns a stream consisting of the non-null IngredientData.
     *
     * @return A stream of non-null IngredientData.
     */
    public Stream<IngredientData> getNonNullIngredients() {
        return Arrays.stream(indexedBySlot).filter(Objects::nonNull);
    }

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
    @Nullable
    public IngredientData getBySlot(int slot) {
        return slot > 0 && slot < indexedBySlot.length ? indexedBySlot[slot] : null;
    }

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
    public List<IngredientData> getBySlots(int[] slots) {
        List<IngredientData> list = new ArrayList<>();
        for (int slot : slots) {
            IngredientData data = getBySlot(slot);
            if (data != null) {
                list.add(data);
            }
        }
        return list;
    }
}
