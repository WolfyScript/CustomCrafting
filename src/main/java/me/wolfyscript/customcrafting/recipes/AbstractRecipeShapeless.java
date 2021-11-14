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
import com.google.common.collect.Queues;
import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public abstract class AbstractRecipeShapeless<C extends AbstractRecipeShapeless<C, S>, S extends CraftingRecipeSettings<S>> extends CraftingRecipe<C, S> {

    private List<Integer> indexes;
    private int nonEmptyIngredientSize;
    private boolean hasAllowedEmptyIngredient;

    protected AbstractRecipeShapeless(NamespacedKey namespacedKey, JsonNode node, int gridSize, Class<S> settingsType) {
        super(namespacedKey, node, gridSize, settingsType);

        JsonNode ingredientNode = node.path(INGREDIENTS_KEY);
        Preconditions.checkArgument(ingredientNode.isObject() || ingredientNode.isArray(), "Error reading ingredients! Ingredient node type must be Array or Object!");
        setIngredients(Streams.stream(node.path(INGREDIENTS_KEY).elements()).map(ItemLoader::loadIngredient).toList());
    }

    protected AbstractRecipeShapeless(NamespacedKey key, int gridSize, S settings) {
        super(key, gridSize, settings);
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

    public void setIngredients(List<Ingredient> ingredients) {
        setIngredients(ingredients.stream());
    }

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
            if(ingredient.getChoices().size() > 1) {
                return ingredient1.getChoices().size() > 1 ? 0 : 1;
            }
            return ingredient1.getChoices().size() > 1 ? -1 : 0;
        });
    }

    @Override
    public boolean fitsDimensions(CraftManager.MatrixData matrixData) {
        return hasAllowedEmptyIngredient ? ( matrixData.getStrippedSize() >= nonEmptyIngredientSize && matrixData.getStrippedSize() <= ingredients.size() ) : matrixData.getStrippedSize() == nonEmptyIngredientSize;
    }

    @Override
    public CraftingData check(CraftManager.MatrixData matrixData) {
        Map<Integer, IngredientData> dataMap = new HashMap<>();
        Deque<Integer> queue = Queues.newArrayDeque(this.indexes);
        List<Integer> usedRecipeSlots = new ArrayList<>();
        ItemStack[] matrix = matrixData.getItems();
        /*
        Previous implementation had the issue that it didn't go through all possible variations and therefore failed to verify the recipe if the items weren't arranged correctly.
        The new implementation should fix that. Of course at the cost of more calculation time... For 9 ingredients not that big of a deal, but for 36, well... that's why 3x3 recipe grids should be the max size possible.
         */
        for (int i = 0; i < matrix.length; i++) { //First we go through all the items in the grid.
            var recipeSlot = checkIngredientNew(i, queue, dataMap, matrix[i]); //Get the slot of the ingredient or -1 if non is found.
            if (recipeSlot == -1) {
                if (i == 0 || usedRecipeSlots.isEmpty()) { //We can directly end the check if it fails for the first slot.
                    return null;
                }
                if (usedRecipeSlots.size() > i) {
                    //Add the previous selected recipe slot back into the queue.
                    queue.addLast(usedRecipeSlots.get(i));
                    usedRecipeSlots.remove(i);
                }
                //Go back one inventory slot
                i -= 2;
                continue;
            } else if (usedRecipeSlots.size() > i) {
                //Add the previous selected recipe slot back into the queue, so we don't miss it.
                queue.addLast(usedRecipeSlots.get(i));
                usedRecipeSlots.remove(i);
            }
            //Add the newly found slot to the used slots.
            usedRecipeSlots.add(recipeSlot);
        }
        if (queue.isEmpty() || (hasAllowedEmptyIngredient && (matrixData.getStrippedSize() == ingredients.size() - queue.size()) && queue.stream().allMatch(index -> ingredients.get(index).isAllowEmpty())) ) {
            //The empty ingredients can be very tricky in shapeless recipes and shouldn't be used... but might as well implement it anyway.
            return new CraftingData(this, dataMap);
        }
        return null;
    }

    protected Integer checkIngredientNew(int pos, Deque<Integer> deque, Map<Integer, IngredientData> dataMap, ItemStack item) {
        int size = deque.size();
        for (int qj = 0; qj < size; qj++) {
            int key = deque.removeFirst(); //Take the first key out of the queue.
            var ingredient = ingredients.get(key);
            Optional<CustomItem> validItem = ingredient.check(item, isExactMeta());
            if (validItem.isPresent()) {
                dataMap.put(pos, new IngredientData(key, ingredient, validItem.get(), item));
                return key;
            }
            //Check failed. Let's add the key back into the queue. (To the end, so we don't check it again and again...)
            deque.addLast(key);
        }
        return -1;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

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
