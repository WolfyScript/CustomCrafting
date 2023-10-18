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

package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import com.wolfyscript.utilities.validator.Validator;
import com.wolfyscript.utilities.validator.ValidatorBuilder;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractRecipeShapeless<C extends AbstractRecipeShapeless<C, S>, S extends CraftingRecipeSettings<S>> extends CraftingRecipe<C, S> {

    @JsonIgnore
    private IntList indexes;
    @JsonIgnore
    private int combinations = 1;
    @JsonIgnore
    private int nonEmptyIngredientSize;
    @JsonIgnore
    private boolean hasAllowedEmptyIngredient;

    protected static <RT extends AbstractRecipeShapeless<?,?>> Validator<RT> validator() {
        return ValidatorBuilder.<RT>object(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "abstract_shapeless_crafting")).def()
                .object(recipe -> recipe.result, resultInitStep -> resultInitStep.use(Result.VALIDATOR))
                .collection(recipe -> recipe.ingredients, init -> init.def().forEach(initEntry -> initEntry.use(Ingredient.VALIDATOR)))
                .build();
    }

    @Deprecated
    protected AbstractRecipeShapeless(NamespacedKey namespacedKey, JsonNode node, int gridSize, Class<S> settingsType) {
        super(namespacedKey, node, gridSize, settingsType);

        JsonNode ingredientNode = node.path(INGREDIENTS_KEY);
        Preconditions.checkArgument(ingredientNode.isObject() || ingredientNode.isArray(), "Error reading ingredients! Ingredient node type must be Array or Object!");
        setIngredients(Streams.stream(node.path(INGREDIENTS_KEY).elements()).map(ItemLoader::loadIngredient).toList());
    }

    protected AbstractRecipeShapeless(NamespacedKey key, CustomCrafting customCrafting, int gridSize, S settings) {
        super(key, customCrafting, gridSize, settings);
    }

    protected AbstractRecipeShapeless(CraftingRecipe<C, S> craftingRecipe) {
        super(craftingRecipe);
    }

    public void addIngredients(Ingredient... ingredients) {
        addIngredients(Arrays.asList(ingredients));
    }

    public void addIngredients(List<Ingredient> ingredients) {
        Preconditions.checkArgument(this.ingredients.size() + ingredients.size() <= maxIngredients, "Recipe cannot have more than " + maxIngredients + " ingredients!");
        List<Ingredient> currentIngredients = new ArrayList<>(this.ingredients);
        currentIngredients.addAll(ingredients);
        setIngredients(currentIngredients);
    }

    public void addIngredient(int count, Ingredient ingredient) {
        Preconditions.checkArgument(ingredients.size() + count <= maxIngredients, "Recipe cannot have more than " + maxIngredients + " ingredients!");
        List<Ingredient> currentIngredients = new ArrayList<>(this.ingredients);
        for (int i = 0; i < count; i++) {
            currentIngredients.add(ingredient);
        }
        setIngredients(currentIngredients);
    }

    public void addIngredient(Ingredient ingredient) {
        addIngredient(1, ingredient);
    }

    @JsonSetter("ingredients")
    public void setIngredients(List<Ingredient> ingredients) {
        setIngredients(ingredients.stream());
    }

    @JsonGetter("ingredients")
    @Override
    public List<Ingredient> getIngredients() {
        return super.getIngredients();
    }

    @JsonIgnore
    public void setIngredients(Stream<Ingredient> ingredients) {
        List<Ingredient> ingredientsNew = ingredients.filter(ingredient -> ingredient != null && !ingredient.isEmpty()).toList();
        Preconditions.checkArgument(!ingredientsNew.isEmpty(), "Invalid ingredients! Recipe requires non-air ingredients!");
        this.ingredients = ingredientsNew;
        this.nonEmptyIngredientSize = (int) this.ingredients.stream().filter(ingredient -> !ingredient.isAllowEmpty()).count();
        this.hasAllowedEmptyIngredient = this.nonEmptyIngredientSize != this.ingredients.size();
        this.indexes = new IntArrayList(this.ingredients.size());
        for (int i = 0; i < this.ingredients.size(); i++) {
            indexes.add(i);
        }
        indexes.sort((index, index1) -> {
            var ingredient = this.ingredients.get(index);
            var ingredient1 = this.ingredients.get(index1);
            if (ingredient.choices().size() > 1) {
                return ingredient1.choices().size() > 1 ? 0 : 1;
            }
            return ingredient1.choices().size() > 1 ? -1 : 0;
        });
        combinations = 1;
        for (Ingredient ingredient : this.ingredients) {
            combinations *= ingredient.size() + 1;
        }
    }

    @Override
    public boolean fitsDimensions(CraftManager.MatrixData matrixData) {
        return hasAllowedEmptyIngredient ? (matrixData.getStrippedSize() >= nonEmptyIngredientSize && matrixData.getStrippedSize() <= ingredients.size()) : matrixData.getStrippedSize() == nonEmptyIngredientSize;
    }

    /**
     * <p>
     * Checks if the recipe matches the given matrix data.
     * </p>
     * <p>
     *     The shapeless recipe algorithm tries to efficiently determine if the correct ingredients are used.<br>
     *     <p>
     *         <h3>Problem:</h3>
     *         Shapeless recipes may have multiple ingredients with multiple variations.
     *         So an item in the matrix may match multiple ingredients.
     *         Due to the non-deterministic initial order of the ingredients in the matrix, the proper order of used ingredients need to be determined when matching it.
     *     </p>
     *     <p>
     *         <h3>Solution:</h3>
     *         The current algorithm acts as a tree that tries to eliminate the paths that cannot occur as far as possible.
     *         It begins at the first slot in the matrix (root) and continuos until it matches the last slot.
     *     </p>
     *     <p>
     *     For each slot it matches the item in that slot to the ingredients:
     *     <ul>
     *         <li>
     *             In case there is a valid ingredient:
     *             <ol>
     *                 <li>The ingredient is marked as checked for the current slot.</li>
     *                 <li>The ingredient is mapped to the current slot.</li>
     *                 <li>The ingredient/slot pair is stored in the queue.</li>
     *                 <li>continuos to the next slot.</li>
     *             </ol>
     *         </li>
     *         <li>
     *            Otherwise:
     *            <ul>
     *                <li>If this slot is the first slot in the matrix (root) it jumps out of the loop.</li>
     *                <li>
     *                    Else:
     *                    <ul>
     *                        <li>Goes back to the previous slot and rechecks it.</li>
     *                    </ul>
     *                </li>
     *            </ul>
     *         </li>
     *     </ul>
     *
     *     </p>
     * </p>
     *
     *
     * @param matrixData The cache of the matrix.
     * @return The data of the matching recipe, or null if not valid.
     */
    @Override
    public CraftingData check(CraftManager.MatrixData matrixData) {
        if (isDisabled() || !fitsDimensions(matrixData)) return null;
        final IngredientData[] dataArray = new IngredientData[ingredients.size()];
        final ItemStack[] matrix = matrixData.getItems();
        final IntList selectedSlots = new IntArrayList(matrix.length);
        final int[] checkedIndicesPerSlot = new int[matrix.length];

        for (int i = 0; i < matrix.length; ) { //First we go through all the items in the grid.
            final int checkedIndices = checkedIndicesPerSlot[i];
            final int recipeSlot = checkIngredient(i, matrixData, selectedSlots, checkedIndices, dataArray, matrix[i]); //Get the slot of the ingredient or -1 if non is found.
            if (recipeSlot == -1) {
                // Invalid ingredient. Does not match current matrix stack.
                if (i == 0 || countOfSetBits(checkedIndices) == indexes.size()) { //We can directly end the check if it fails for the first slot.
                    return null;
                }
                if (selectedSlots.size() > i) {
                    selectedSlots.rem(i); //Add the previous selected recipe slot back into the queue.
                }
                i--; //Go back to previous slot and recheck it.
                continue;
            } else if (selectedSlots.size() > i) {
                selectedSlots.set(i, recipeSlot); //Add the previous selected recipe slot back into the queue, so we don't miss it.
            } else {
                selectedSlots.add(recipeSlot); //Add the newly found slot to the used slots.
            }
            checkedIndicesPerSlot[i] = setBit(checkedIndices, recipeSlot); // Ingredient matches current matrix stack, goto next slot
            i++;
        }
        if ((selectedSlots.size() == ingredients.size())) {
            return new CraftingData(this, dataArray);
        }
        if (hasAllowedEmptyIngredient && matrixData.getStrippedSize() == selectedSlots.size()) { //The empty ingredients can be very tricky in shapeless recipes and shouldn't be used... but might as well implement it anyway.
            if (indexes.intStream().filter(index -> !selectedSlots.contains(index)).allMatch(index -> ingredients.get(index).isAllowEmpty())) {
                return new CraftingData(this, dataArray);
            }
        }
        return null;
    }

    private static int setBit(int bitSet, int index) {
        return bitSet | (1<<index);
    }

    private static boolean getBit(int bitSet, int index) {
        return (bitSet & (1<<index)) != 0;
    }

    private static int countOfSetBits(int bitSet) {
        return Integer.bitCount(bitSet);
    }

    /**
     * <p>
     * Checks the ingredient at the given position of the matrix and compares it to the available ingredients.
     * The ingredients that are already matched against another slot, or were already matched against this position, are skipped.
     * </p>
     * <p><b>Side-effect:</b>
     * Once a match is found it is written into the dataArray to cache it (which will be used for the merge operations and shrink methods).
     * </p>
     * @param pos The position (slot) in the matrix.
     * @param matrixData The cached contents of the matrix.
     * @param selectedSlots The slots that are already matched to an ingredient.
     * @param checkedIndices The ingredient indices already matched against to this position (slot).
     * @param dataArray The cache data for the final recipe data.
     * @param item The item at the given position in the matrix.
     * @return The ingredient index that matches this position.
     */
    protected int checkIngredient(int pos, CraftManager.MatrixData matrixData, List<Integer> selectedSlots, Integer checkedIndices, IngredientData[] dataArray, ItemStack item) {
        for (int key : indexes) {
            if (!selectedSlots.contains(key) && !getBit(checkedIndices, key)) {
                final var ingredient = ingredients.get(key);
                final var checkResult = ingredient.checkChoices(item, isCheckNBT());
                if (checkResult.isPresent()) {
                    //For shapeless we can't actually determine the exact inventory slot of the ingredient (without massively increasing complexity), but we can make an estimate using the same tactic as with shaped recipes.
                    //Though, Items will still be slightly rearranged in the matrix.
                    dataArray[key] = new IngredientData(
                            pos + (matrixData.getOffsetX() + (matrixData.getOffsetY() * matrixData.getGridSize())) + ((pos / maxGridDimension) * (matrixData.getGridSize() - matrixData.getWidth())),
                            key, ingredient, checkResult.get(), new ItemStack(item)
                    );
                    return key;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    @Deprecated
    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectField(INGREDIENTS_KEY, this.ingredients);
    }

    @Override
    public void writeToBuf(MCByteBuf byteBuf) {
        super.writeToBuf(byteBuf);
        byteBuf.writeVarInt(ingredients.size());
        ingredients.forEach(ingredient -> {
            byteBuf.writeVarInt(ingredient.size());
            for (StackReference choice : ingredient.choices()) {
                byteBuf.writeItemStack(choice.identifier().item());
            }
        });
    }
}
