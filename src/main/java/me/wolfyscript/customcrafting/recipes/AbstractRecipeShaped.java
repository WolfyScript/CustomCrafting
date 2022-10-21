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
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.RecipeUtil;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@JsonPropertyOrder(value = {"@type", "group", "hidden", "vanillaBook", "priority", "checkNBT", "conditions", "symmetry", "keepShapeAsIs", "shape", "ingredients"})
public abstract class AbstractRecipeShaped<C extends AbstractRecipeShaped<C, S>, S extends CraftingRecipeSettings<S>> extends CraftingRecipe<C, S> {

    private static final String SHAPE_KEY = "shape";
    private static final String MIRROR_KEY = "mirror";
    private static final String HORIZONTAL_KEY = "horizontal";
    private static final String VERTICAL_KEY = "vertical";
    private static final String ROTATION_KEY = "rotation";

    protected Map<Character, Ingredient> mappedIngredients;
    @JsonIgnore
    private Shape internalShape;
    private boolean keepShapeAsIs = false;
    private String[] shape;
    private final Symmetry symmetry;

    protected AbstractRecipeShaped(NamespacedKey namespacedKey, JsonNode node, int gridSize, Class<S> settingsType) {
        super(namespacedKey, node, gridSize, settingsType);
        this.symmetry = Symmetry.ofLegacy(node.path(MIRROR_KEY));
        this.mappedIngredients = Map.of();

        Map<Character, Ingredient> loadedIngredients = Streams.stream(node.path(INGREDIENTS_KEY).fields()).collect(Collectors.toMap(entry -> entry.getKey().charAt(0), entry -> ItemLoader.loadIngredient(entry.getValue())));
        if (node.has(SHAPE_KEY)) {
            setShape(mapper.convertValue(node.path(SHAPE_KEY), String[].class));
        } else {
            generateMissingShape(List.copyOf(loadedIngredients.keySet()));
        }
        setIngredients(loadedIngredients);
    }

    protected AbstractRecipeShaped(NamespacedKey key, CustomCrafting customCrafting, Symmetry symmetry, boolean keepShapeAsIs, String[] shape, int gridSize, S settings) {
        this(key, customCrafting, symmetry, keepShapeAsIs, gridSize, settings);
        setShape(shape);
    }

    protected AbstractRecipeShaped(NamespacedKey key, CustomCrafting customCrafting, Symmetry symmetry, boolean keepShapeAsIs, int gridSize, S settings) {
        super(key, customCrafting, gridSize, settings);
        this.keepShapeAsIs = keepShapeAsIs;
        this.symmetry = symmetry;
        this.mappedIngredients = new HashMap<>();
    }

    protected AbstractRecipeShaped(AbstractRecipeShaped<C, S> recipe) {
        super(recipe);
        this.keepShapeAsIs = recipe.keepShapeAsIs;
        this.symmetry = recipe.symmetry.copy();
        this.mappedIngredients = new HashMap<>();
        setShape(recipe.shape.clone());
        setIngredients(recipe.mappedIngredients.entrySet().stream().map(entry -> Map.entry(entry.getKey(), entry.getValue().clone())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Deprecated
    public void setMirrorHorizontal(boolean mirrorHorizontal) {
        symmetry.setHorizontal(mirrorHorizontal);
    }

    @Deprecated
    public void setMirrorVertical(boolean mirrorVertical) {
        symmetry.setVertical(mirrorVertical);
    }

    @Deprecated
    public void setMirrorRotation(boolean mirrorRotation) {
        symmetry.setRotate(mirrorRotation);
    }

    @Deprecated
    public boolean mirrorHorizontal() {
        return symmetry.isHorizontal();
    }

    @Deprecated
    public boolean mirrorVertical() {
        return symmetry.isVertical();
    }

    @Deprecated
    public boolean mirrorRotation() {
        return symmetry.isRotate();
    }

    public Symmetry getSymmetry() {
        return symmetry;
    }

    public String[] getShape() {
        return shape;
    }

    public boolean isKeepShapeAsIs() {
        return keepShapeAsIs;
    }

    /**
     * Sets the shape of the recipe and generates all the possible variations based on the mirror settings.<br>
     * <br>
     * The shape is shrunk to the smallest possible width and height.<br>
     * Besides the shape, it generates a flat list of the ingredients based on the shrunk shape.<br>
     * <br>
     * <b>{@link #setMirrorHorizontal(boolean)}, {@link #setMirrorVertical(boolean)}, and {@link #setMirrorRotation(boolean)} must be invoked before this method so their settings have an effect on the generated shape!</b>
     *
     * @param shape The shape of the recipe
     */
    @JsonSetter
    public void setShape(@NotNull String... shape) {
        Preconditions.checkArgument(shape != null && shape.length > 0, "Shape can not be null!");
        Preconditions.checkArgument(shape.length <= maxGridDimension, "Shape must not have more than " + maxGridDimension + " rows!");
        int currentWidth = -1;
        for (String row : shape) {
            Preconditions.checkArgument(Objects.requireNonNull(row, "Shape row cannot be null!").length() <= maxGridDimension, "Shape row must not be longer than " + maxGridDimension + "!");
            Preconditions.checkArgument(currentWidth == -1 || currentWidth == row.length(), "Shape must be rectangular!");
            currentWidth = row.length();
        }
        this.shape = keepShapeAsIs ? shape : RecipeUtil.formatShape(shape).toArray(new String[0]);
        var flattenShape = String.join("", this.shape);
        Preconditions.checkArgument(!flattenShape.isEmpty() && !flattenShape.isBlank(), "Shape must not be empty! (Shape: \"" + Arrays.toString(this.shape) + "\")!");
        Map<Character, Ingredient> newIngredients = new HashMap<>();
        flattenShape.chars().mapToObj(value -> (char) value).forEach(character -> newIngredients.put(character, this.mappedIngredients.get(character)));
        this.mappedIngredients = newIngredients;
    }

    public Shape getInternalShape() {
        return internalShape;
    }

    private void createFlatIngredients() {
        //Create flatten ingredients. This makes it possible to use a key multiple times in one shape.
        var flattenShape = String.join("", this.shape);
        Preconditions.checkArgument(!flattenShape.isEmpty() && !flattenShape.isBlank(), "Shape must not be empty! (Shape: \"" + Arrays.toString(this.shape) + "\")!");
        this.ingredients = flattenShape.chars().mapToObj(key -> mappedIngredients.getOrDefault((char) key, new Ingredient())).toList();
        //Create internal shape, which is more performant when used in checks later on.
        this.internalShape = new Shape();
    }

    /**
     * Generates the shape of the given keys.
     *
     * @param keys
     * @return
     */
    public void generateMissingShape(List<Character> keys) {
        var genShape = new String[maxGridDimension];
        var index = 0;
        var row = 0;
        for (int i = 0; i < maxIngredients; i++) {
            final var ingrd = CraftingRecipe.LETTERS.charAt(i);
            final var current = genShape[row] != null ? genShape[row] : "";
            if (!keys.contains(ingrd)) {
                genShape[row] = current + " ";
            } else {
                genShape[row] = current + ingrd;
            }
            if (++index % maxGridDimension == 0) {
                row++;
            }
        }
        setShape(genShape);
    }

    public boolean isShapeless() {
        return false;
    }

    public void setIngredient(char key, @NotNull Ingredient ingredient) {
        Preconditions.checkArgument(this.mappedIngredients.containsKey(key), "Invalid ingredient key! Shape does not contain key!");
        Preconditions.checkArgument(ingredient != null && !ingredient.isEmpty(), "Invalid ingredient! Ingredient must not be null nor empty!");
        ingredient.buildChoices();
        this.mappedIngredients.put(key, ingredient);
        createFlatIngredients();
    }

    @JsonSetter("ingredients")
    public void setIngredients(Map<Character, Ingredient> ingredients) {
        this.mappedIngredients = ingredients.entrySet().stream().filter(entry -> {
            var ingredient = entry.getValue();
            if (ingredient != null) {
                ingredient.buildChoices();
                return !ingredient.isEmpty();
            }
            return false;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Preconditions.checkArgument(!this.mappedIngredients.isEmpty(), "Invalid ingredients! Recipe must have non-air ingredients!");
        createFlatIngredients();
    }

    @JsonIgnore
    @Override
    public List<Ingredient> getIngredients() {
        return super.getIngredients();
    }

    /**
     * @return An unmodifiable Map copy of the Ingredients mapped to the character in the shape.
     */
    @JsonGetter("ingredients")
    public Map<Character, Ingredient> getMappedIngredients() {
        return Map.copyOf(mappedIngredients);
    }

    @Override
    public boolean fitsDimensions(@NotNull CraftManager.MatrixData matrixData) {
        if (keepShapeAsIs) {
            return ingredients.size() == matrixData.getOriginalMatrix().length && internalShape.getHeight() == matrixData.getGridSize() && internalShape.getWidth() == matrixData.getGridSize();
        }
        return ingredients.size() == matrixData.getMatrix().length && internalShape.getHeight() == matrixData.getHeight() && internalShape.getWidth() == matrixData.getWidth();
    }

    @Override
    public CraftingData check(CraftManager.MatrixData matrixData) {
        if (internalShape.getWidth() == matrixData.getWidth() && internalShape.getHeight() == matrixData.getHeight()) {
            for (int[] entry : internalShape.getUniqueShapes()) {
                var craftingData = checkShape(matrixData, entry);
                if (craftingData != null) return craftingData;
            }
        }
        return null;
    }

    protected CraftingData checkShape(@NotNull CraftManager.MatrixData matrixData, int[] shape) {
        Map<Integer, IngredientData> dataMap = new HashMap<>();
        var i = 0;
        ItemStack[] matrix = keepShapeAsIs ? matrixData.getOriginalMatrix() : matrixData.getMatrix();
        for (ItemStack invItem : matrix) {
            int recipeSlot = shape[i];
            if (invItem != null) {
                if (recipeSlot >= 0) {
                    var ingredient = ingredients.get(recipeSlot);
                    if (ingredient != null) {
                        Optional<CustomItem> item = ingredient.check(invItem, this.checkAllNBT);
                        if (item.isPresent()) {
                            //In order to index the ingredients for the correct inventory slot we need to reverse the shape offset.
                            int estimatedSlot;
                            if (keepShapeAsIs) {
                                estimatedSlot = i;
                            } else {
                                int row = i / getInternalShape().getWidth();
                                int offset = keepShapeAsIs ? 0 : matrixData.getOffsetX() + (matrixData.getOffsetY() * matrixData.getGridSize());
                                estimatedSlot = i + offset + (row * (matrixData.getGridSize() - matrixData.getWidth()));
                            }
                            dataMap.put(estimatedSlot, new IngredientData(recipeSlot, ingredient, item.get(), new ItemStack(invItem)));
                            i++;
                            continue;
                        }
                    }
                }
                return null;
            } else if (recipeSlot >= 0) {
                return null;
            }
            i++;
        }
        return new CraftingData(this, dataMap);
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        if (!ingredients.isEmpty()) {
            ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(maxIngredients))).setVariants(guiHandler, this.getResult());
            //Center Recipe as best as possible
            // 3 - 1 = 2 / 2 = 1 -> _x_     4 - 1 = 3 / 2 = 1 -> _x__
            // 3 - 2 = 1 / 2 = 0 -> xx_     4 - 2 = 2 / 2 = 1 -> _xx_
            //                              4 - 3 = 1 / 2 = 0 -> xxx_
            //
            // 5 - 1 = 4 / 2 = 2 -> __x__   6 - 5 = 1 / 2 = 0 -> xxxxx_
            // 5 - 2 = 3 / 2 = 1 -> _xx__   6 - 4 = 2 / 2 = 1 -> _xxxx_
            // 5 - 3 = 2 / 2 = 1 -> _xxx_   6 - 3 = 3 / 2 = 1 -> _xxx__
            // 5 - 4 = 1 / 2 = 0 -> xxxx_   6 - 2 = 4 / 2 = 2 -> __xx__
            //                              6 - 1 = 5 / 2 = 2 -> __x___
            int rowOffset = (maxGridDimension - internalShape.getWidth()) / 2;
            int columnOffset = (maxGridDimension - internalShape.getHeight()) / 2;
            int rowLimit = internalShape.width + rowOffset;
            int columnLimit = internalShape.height + columnOffset;
            int i = (columnOffset * maxGridDimension) + rowOffset;
            int ingredientIndex = 0;
            for (int r = columnOffset; r < maxGridDimension; r++) {
                for (int c = rowOffset; c < maxGridDimension; c++) {
                    if (c < rowLimit && r < columnLimit && ingredientIndex < ingredients.size()) {
                        var ingredient = ingredients.get(ingredientIndex);
                        if (ingredient != null) {
                            ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(i))).setVariants(guiHandler, ingredient);
                        }
                        ingredientIndex++;
                    }
                    i++;
                }
                i += rowOffset;
            }
        }
    }

    @Deprecated
    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectField(SHAPE_KEY, shape);
        gen.writeObjectFieldStart(MIRROR_KEY);
        gen.writeBooleanField(HORIZONTAL_KEY, symmetry.horizontal);
        gen.writeBooleanField(VERTICAL_KEY, symmetry.vertical);
        gen.writeBooleanField(ROTATION_KEY, symmetry.rotate);
        gen.writeEndObject();
        gen.writeObjectField(INGREDIENTS_KEY, this.mappedIngredients);
    }

    @Override
    public void writeToBuf(MCByteBuf byteBuf) {
        super.writeToBuf(byteBuf);
        byteBuf.writeVarInt(shape.length);
        for (String s : shape) {
            byteBuf.writeUtf(s, maxGridDimension);
        }
        internalShape.writeToBuf(byteBuf);
    }

    /**
     * This generates and stores the flipped states of the recipe shape.<br>
     * This pre-calculates the different states of the recipe on start-up for better performance on runtime.
     */
    public class Shape {

        private final int width;
        private final int height;

        private final Set<int[]> entries;

        /**
         * This constructor performs a very resource intensive calculation <br>
         * to generate, flip, and flatten the original shape <br>
         * to multiple int only arrays of different possible states the recipe can be in.
         */
        public Shape() {
            Set<int[]> shapeEntryList = new HashSet<>();
            //Original shape
            final var original2d = new int[shape.length][shape[0].length()];
            var index = 0;
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length(); j++) {
                    original2d[i][j] = shape[i].charAt(j) != ' ' ? index : -1;
                    index++;
                }
            }
            this.height = original2d.length;
            this.width = original2d[0].length;
            apply(shapeEntryList, original2d);
            if (symmetry.horizontal) {
                final int[][] flippedHorizontally2d = original2d.clone();
                for (int[] ints : flippedHorizontally2d) {
                    ArrayUtils.reverse(ints);
                }
                apply(shapeEntryList, flippedHorizontally2d);
            }
            if (symmetry.vertical) {
                final int[][] flippedVertically2d = original2d.clone();
                ArrayUtils.reverse(flippedVertically2d);
                apply(shapeEntryList, flippedVertically2d);
                if (symmetry.rotate) {
                    int[][] rotated = flippedVertically2d.clone();
                    for (int[] ints : rotated) {
                        ArrayUtils.reverse(ints);
                    }
                    apply(shapeEntryList, rotated);
                }
            }
            //Makes sure to make the set unmodifiable!
            this.entries = Set.copyOf(shapeEntryList);
        }

        /**
         * Applies the flattened shape array to the set.
         * <br>
         * Because flat ingredients of different flipped/rotated shapes can be the same, <br>
         * we don't need to check the same shape twice.
         *
         * @param entries The modifiable set of entries.
         * @param array   The shape array to flatten.
         */
        private void apply(Set<int[]> entries, int[][] array) {
            entries.add(Stream.of(array).flatMapToInt(IntStream::of).toArray());
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        /**
         * @return An unmodifiable set of all the states the recipe can be crafted in.
         */
        public Set<int[]> getUniqueShapes() {
            return entries;
        }

        @Override
        public String toString() {
            return "Shape{" +
                    "entries=" + entries +
                    '}';
        }

        public void writeToBuf(MCByteBuf byteBuf) {
            byteBuf.writeInt(width);
            byteBuf.writeInt(height);
            byteBuf.writeVarInt(entries.size());
            entries.forEach(byteBuf::writeVarIntArray);
        }

    }

    public static class Symmetry {

        private boolean horizontal;
        private boolean vertical;
        private boolean rotate;

        Symmetry() {
            this.horizontal = false;
            this.vertical = false;
            this.rotate = false;
        }

        private Symmetry(Symmetry other) {
            this.horizontal = other.horizontal;
            this.vertical = other.vertical;
            this.rotate = other.rotate;
        }

        private static Symmetry ofLegacy(JsonNode node) {
            var symmetry = new Symmetry();
            symmetry.horizontal = node.path(HORIZONTAL_KEY).asBoolean(false);
            symmetry.vertical = node.path(VERTICAL_KEY).asBoolean(false);
            symmetry.rotate = node.path(ROTATION_KEY).asBoolean(false);
            return symmetry;
        }

        public void setHorizontal(boolean horizontal) {
            this.horizontal = horizontal;
        }

        public boolean isHorizontal() {
            return horizontal;
        }

        public void setVertical(boolean vertical) {
            this.vertical = vertical;
        }

        public boolean isVertical() {
            return vertical;
        }

        public void setRotate(boolean rotate) {
            this.rotate = rotate;
        }

        public boolean isRotate() {
            return rotate;
        }

        public Symmetry copy() {
            return new Symmetry(this);
        }

    }
}
