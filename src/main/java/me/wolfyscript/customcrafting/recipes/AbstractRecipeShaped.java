package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.RecipeUtil;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class AbstractRecipeShaped<C extends AbstractRecipeShaped<C, S>, S extends CraftingRecipeSettings<S>> extends CraftingRecipe<C, S> {

    private String[] shape;
    private Shape internalShape;
    private boolean mirrorHorizontal;
    private boolean mirrorVertical;
    private boolean mirrorRotation;

    protected AbstractRecipeShaped(NamespacedKey namespacedKey, JsonNode node, int gridSize, Class<S> settingsType) {
        super(namespacedKey, node, gridSize, settingsType);
        JsonNode mirrorNode = node.path("mirror");
        this.mirrorHorizontal = mirrorNode.path("horizontal").asBoolean(true);
        this.mirrorVertical = mirrorNode.path("vertical").asBoolean(false);
        this.mirrorRotation = mirrorNode.path("rotation").asBoolean(false);
    }

    protected AbstractRecipeShaped(NamespacedKey key, int gridSize, S settings) {
        super(key, gridSize, settings);
        this.mirrorHorizontal = true;
        this.mirrorVertical = false;
        this.mirrorRotation = false;
    }

    protected AbstractRecipeShaped(CraftingRecipe<?, S> craftingRecipe) {
        super(craftingRecipe);
        if (craftingRecipe instanceof AbstractRecipeShaped<?, ?> shapedRecipe) {
            this.mirrorHorizontal = shapedRecipe.mirrorHorizontal;
            this.mirrorVertical = shapedRecipe.mirrorVertical;
            this.mirrorRotation = shapedRecipe.mirrorRotation;
        } else {
            this.mirrorHorizontal = true;
            this.mirrorVertical = false;
            this.mirrorRotation = false;
        }
    }

    public String[] getShape() {
        return shape;
    }

    public Shape getInternalShape() {
        return internalShape;
    }

    public void setMirrorHorizontal(boolean mirrorHorizontal) {
        this.mirrorHorizontal = mirrorHorizontal;
    }

    public void setMirrorVertical(boolean mirrorVertical) {
        this.mirrorVertical = mirrorVertical;
    }

    public void setMirrorRotation(boolean mirrorRotation) {
        this.mirrorRotation = mirrorRotation;
    }

    public boolean mirrorHorizontal() {
        return mirrorHorizontal;
    }

    public boolean mirrorVertical() {
        return mirrorVertical;
    }

    public boolean mirrorRotation() {
        return mirrorRotation;
    }

    /**
     * Generates the shapes for all the possible ways the shape can be mirrored and rotated. <br>
     * The shapes are shrunk to the smallest possible width and height.<br>
     * Besides the shape it generates a flat list of the ingredients based on the shrunk shape.
     */
    @Override
    public void constructRecipe() {
        if (this.shape == null) {
            this.shape = generateMissingShape();
        }
        this.shape = RecipeUtil.formatShape(this.shape).toArray(new String[0]);
        //Create flatten ingredients. This makes it possible to use a key multiple times in one shape.
        var flattenShape = String.join("", this.shape);
        Preconditions.checkArgument(!flattenShape.isEmpty() && !flattenShape.isBlank(), "Empty shape \"" + Arrays.toString(this.shape) + "\"!");
        this.ingredientsFlat = flattenShape.chars().mapToObj(key -> getIngredients().getOrDefault((char) key, new Ingredient())).toList();
        //Create internal shape, which is more performant when used in checks later on.
        this.internalShape = new Shape();
    }

    private String[] generateMissingShape() {
        var genShape = new String[requiredGridSize];
        var index = 0;
        var row = 0;
        for (int i = 0; i < bookSquaredGrid; i++) {
            var ingrd = ICraftingRecipe.LETTERS.charAt(i);
            var items = getIngredients().get(ingrd);
            final var current = genShape[row] != null ? genShape[row] : "";
            if (items == null || items.isEmpty()) {
                genShape[row] = current + " ";
            } else {
                genShape[row] = current + ingrd;
            }
            index++;
            if ((index % requiredGridSize) == 0) {
                row++;
            }
        }
        return genShape;
    }

    @Override
    public boolean isShapeless() {
        return false;
    }

    @Override
    public boolean fitsDimensions(CraftManager.MatrixData matrixData) {
        return ingredientsFlat.size() == matrixData.getMatrix().length && internalShape.height == matrixData.getHeight() && internalShape.width == matrixData.getWidth();
    }

    @Override
    public CraftingData check(CraftManager.MatrixData matrixData) {
        for (int[] entry : internalShape.getUniqueShapes()) {
            var craftingData = checkShape(matrixData, entry);
            if (craftingData != null) return craftingData;
        }
        return null;
    }

    protected CraftingData checkShape(CraftManager.MatrixData matrixData, int[] shape) {
        Map<Integer, IngredientData> dataMap = new HashMap<>();
        var i = 0;
        for (ItemStack invItem : matrixData.getMatrix()) {
            int slot = shape[i];
            if (invItem != null) {
                if (slot >= 0) {
                    var ingredient = ingredientsFlat.get(slot);
                    if (ingredient != null) {
                        Optional<CustomItem> item = ingredient.check(invItem, this.exactMeta);
                        if (item.isPresent()) {
                            dataMap.put(i, new IngredientData(slot, ingredient, item.get(), invItem));
                            i++;
                            continue;
                        }
                    }
                }
                return null;
            } else if (slot >= 0) {
                return null;
            }
            i++;
        }
        return new CraftingData(this, dataMap);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectField("shape", shape);
        gen.writeObjectFieldStart("mirror");
        gen.writeBooleanField("horizontal", this.mirrorHorizontal);
        gen.writeBooleanField("vertical", this.mirrorVertical);
        gen.writeBooleanField("rotation", this.mirrorRotation);
        gen.writeEndObject();
    }

    @Override
    public void writeToBuf(MCByteBuf byteBuf) {
        super.writeToBuf(byteBuf);
        byteBuf.writeVarInt(shape.length);
        for (String s : shape) {
            byteBuf.writeUtf(s, 3);
        }
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

            if (mirrorHorizontal) {
                final int[][] flippedHorizontally2d = original2d.clone();
                for (int[] ints : flippedHorizontally2d) {
                    ArrayUtils.reverse(ints);
                }
                apply(shapeEntryList, flippedHorizontally2d);
            }

            if (mirrorVertical) {
                final int[][] flippedVertically2d = original2d.clone();
                ArrayUtils.reverse(flippedVertically2d);
                apply(shapeEntryList, flippedVertically2d);
                if (mirrorRotation) {
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

    }
}
