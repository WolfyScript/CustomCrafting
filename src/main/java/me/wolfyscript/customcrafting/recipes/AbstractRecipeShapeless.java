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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.ItemLoader;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractRecipeShapeless<C extends AbstractRecipeShapeless<C, S>, S extends CraftingRecipeSettings<S>> extends CraftingRecipe<C, S> {

    @JsonIgnore
    private List<Integer> indexes;
    @JsonIgnore
    private int combinations = 1;
    @JsonIgnore
    private int nonEmptyIngredientSize;
    @JsonIgnore
    private boolean hasAllowedEmptyIngredient;

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
        this.indexes = new ArrayList<>();
        for (int i = 0; i < this.ingredients.size(); i++) {
            indexes.add(i);
        }
        indexes.sort((index, index1) -> {
            var ingredient = this.ingredients.get(index);
            var ingredient1 = this.ingredients.get(index1);
            if (ingredient.getChoices().size() > 1) {
                return ingredient1.getChoices().size() > 1 ? 0 : 1;
            }
            return ingredient1.getChoices().size() > 1 ? -1 : 0;
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

    @Override
    public CraftingData check(CraftManager.MatrixData matrixData) {
        final Map<Integer, IngredientData> dataMap = new HashMap<>();
        final List<Integer> selectedSlots = new LinkedList<>();
        final Multimap<Integer, Integer> checkedSlots = HashMultimap.create(ingredients.size(), ingredients.size());
        final ItemStack[] matrix = matrixData.getItems();
        /*
        Previous implementation had the issue that it didn't go through all possible variations and therefore failed to verify the recipe if the items weren't arranged correctly.
        The new implementation should fix that. Of course at the cost of more calculation time... For 9 ingredients not that big of a deal, but for 36, well... that's why 3x3 recipe grids should be the max size possible.
         */
        for (int i = 0; i < matrix.length; i++) { //First we go through all the items in the grid.
            var checked = checkedSlots.get(i);
            var recipeSlot = checkIngredientNew(i, matrixData, selectedSlots, checked, dataMap, matrix[i]); //Get the slot of the ingredient or -1 if non is found.
            if (recipeSlot == -1) {
                if (i == 0 || checked.size() == indexes.size()) { //We can directly end the check if it fails for the first slot.
                    return null;
                }
                if (selectedSlots.size() > i) {
                    selectedSlots.remove(i); //Add the previous selected recipe slot back into the queue.
                }
                i -= 2; //Go back one inventory slot
                continue;
            } else if (selectedSlots.size() > i) {
                selectedSlots.set(i, recipeSlot);//Add the previous selected recipe slot back into the queue, so we don't miss it.
            } else {
                selectedSlots.add(recipeSlot);//Add the newly found slot to the used slots.
            }
            checkedSlots.put(i, recipeSlot);
        }
        if ((selectedSlots.size() == ingredients.size())) {
            return new CraftingData(this, dataMap);
        }
        if (hasAllowedEmptyIngredient && matrixData.getStrippedSize() == selectedSlots.size()) { //The empty ingredients can be very tricky in shapeless recipes and shouldn't be used... but might as well implement it anyway.
            if (indexes.stream().filter(index -> !selectedSlots.contains(index)).allMatch(index -> ingredients.get(index).isAllowEmpty())) {
                return new CraftingData(this, dataMap);
            }
        }
        return null;
    }

    protected Integer checkIngredientNew(int pos, CraftManager.MatrixData matrixData, List<Integer> selectedSlots, Collection<Integer> checkedSlots, Map<Integer, IngredientData> dataMap, ItemStack item) {
        for (int key : indexes) {
            if (!selectedSlots.contains(key) && !checkedSlots.contains(key)) {
                var ingredient = ingredients.get(key);
                Optional<CustomItem> validItem = ingredient.check(item, isCheckNBT());
                if (validItem.isPresent()) {
                    //For shapeless we can't actually determine the exact inventory slot of the ingredient (without massively increasing complexity), but we can make an estimate using the same tactic as with shaped recipes.
                    //Though, Items will still be slightly rearranged in the matrix.
                    int row = pos / maxGridDimension;
                    int offset = matrixData.getOffsetX() + (matrixData.getOffsetY() * matrixData.getGridSize());
                    dataMap.put(pos + offset + (row * (matrixData.getGridSize() - matrixData.getWidth())), new IngredientData(key, ingredient, validItem.get(), new ItemStack(item)));
                    return key;
                }
                //Check failed. Let's add the key back into the queue. (To the end, so we don't check it again and again...)
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
            for (CustomItem choice : ingredient.getChoices()) {
                byteBuf.writeItemStack(choice.create());
            }
        });
    }
}
