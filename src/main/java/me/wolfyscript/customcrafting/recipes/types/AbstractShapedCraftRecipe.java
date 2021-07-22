package me.wolfyscript.customcrafting.recipes.types;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
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

public abstract class AbstractShapedCraftRecipe<C extends AbstractShapedCraftRecipe<C>> extends CraftingRecipe<C> {

    private String[] shape;
    private Shape internalShape;
    private boolean mirrorHorizontal;
    private boolean mirrorVertical;
    private boolean mirrorRotation;

    protected AbstractShapedCraftRecipe(NamespacedKey namespacedKey, JsonNode node, int gridSize) {
        super(namespacedKey, node, gridSize);
        JsonNode mirrorNode = node.path("mirror");
        this.mirrorHorizontal = mirrorNode.path("horizontal").asBoolean(true);
        this.mirrorVertical = mirrorNode.path("vertical").asBoolean(false);
        this.mirrorRotation = mirrorNode.path("rotation").asBoolean(false);
    }

    protected AbstractShapedCraftRecipe(int gridSize) {
        super(gridSize);
        this.mirrorHorizontal = true;
        this.mirrorVertical = false;
        this.mirrorRotation = false;
    }

    protected AbstractShapedCraftRecipe(CraftingRecipe<?> craftingRecipe) {
        super(craftingRecipe);
        if (craftingRecipe instanceof AbstractShapedCraftRecipe<?> shapedRecipe) {
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

        this.ingredientsFlat = new ArrayList<>();
        for (char key : flattenShape.toCharArray()) {
            ingredientsFlat.add(getIngredients().getOrDefault(key, new Ingredient()));
        }

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
    public CraftingData check(List<ItemStack> flatMatrix) {
        var craftingData = checkShape(flatMatrix, internalShape.original);
        if (craftingData == null) {
            if (mirrorHorizontal()) {
                craftingData = checkShape(flatMatrix, internalShape.flippedHorizontally);
                if (craftingData != null) {
                    return craftingData;
                }
            }
            if (mirrorVertical()) {
                craftingData = checkShape(flatMatrix, internalShape.flippedVertically);
                if (craftingData != null) {
                    return craftingData;
                }
            }
            if (mirrorHorizontal() && mirrorVertical() && mirrorRotation()) {
                craftingData = checkShape(flatMatrix, internalShape.rotated);
                return craftingData;
            }
        }
        return craftingData;
    }

    protected CraftingData checkShape(List<ItemStack> flatMatrix, int[] internalShape) {
        if (ingredientsFlat == null || ingredientsFlat.isEmpty() || flatMatrix.size() != internalShape.length) {
            return null;
        }
        Map<Integer, IngredientData> dataMap = new HashMap<>();
        for (var i = 0; i < flatMatrix.size(); i++) {
            if (flatMatrix.get(i) != null) {
                int slot = internalShape[i];
                if (slot >= 0) {
                    var ingredient = ingredientsFlat.get(slot);
                    if (ingredient != null) {
                        ItemStack targetItem = flatMatrix.get(i);
                        Optional<CustomItem> item = ingredient.check(targetItem, this.exactMeta);
                        if (item.isPresent()) {
                            dataMap.put(i, new IngredientData(slot, ingredient, item.get(), targetItem));
                            continue;
                        }
                    }
                }
                return null;
            } else if (internalShape[i] >= 0) {
                return null;
            }
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
     * This generates and stores the flipped states of the recipe shape.
     * We trade more calculation on start-up for better performance later on.
     */
    public class Shape {

        private final int[] original;
        private final int[] flippedVertically;
        private final int[] flippedHorizontally;
        private final int[] rotated;

        /**
         * This constructor performs a very resource intensive calculation <br>
         * to generate, flip, and flatten the original shape <br>
         * to multiple int only arrays of different possible states of the recipe.
         */
        public Shape() {
            //Original shape
            final var original2d = new int[shape.length][shape[0].length()];
            var index = 0;
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length(); j++) {
                    original2d[i][j] = shape[i].charAt(j) != ' ' ? index : -1;
                    index++;
                }
            }
            this.original = flatten(original2d);

            final int[][] flippedVertically2d = original2d.clone();
            ArrayUtils.reverse(flippedVertically2d);
            this.flippedVertically = flatten(flippedVertically2d);

            final int[][] flippedHorizontally2d = original2d.clone();
            for (int[] ints : flippedHorizontally2d) {
                ArrayUtils.reverse(ints);
            }
            this.flippedHorizontally = flatten(flippedHorizontally2d);

            int[][] rotated = flippedVertically2d.clone();
            for (int[] ints : rotated) {
                ArrayUtils.reverse(ints);
            }
            this.rotated = flatten(rotated);
        }

        private static int[] flatten(int[][] arr) {
            return Stream.of(arr).flatMapToInt(IntStream::of).toArray();
        }

        public int[] getOriginal() {
            return original;
        }

        public int[] getFlippedHorizontally() {
            return flippedHorizontally;
        }

        public int[] getFlippedVertically() {
            return flippedVertically;
        }

        public int[] getRotated() {
            return rotated;
        }

        @Override
        public String toString() {
            return "Shape{" +
                    "original=" + Arrays.toString(original) +
                    ", flippedVertically=" + Arrays.toString(flippedVertically) +
                    ", flippedHorizontally=" + Arrays.toString(flippedHorizontally) +
                    ", rotated=" + Arrays.toString(rotated) +
                    '}';
        }
    }

}
