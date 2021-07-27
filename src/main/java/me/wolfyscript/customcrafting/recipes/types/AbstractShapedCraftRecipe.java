package me.wolfyscript.customcrafting.recipes.types;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.types.crafting.CraftingRecipeSettings;
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

public abstract class AbstractShapedCraftRecipe<C extends AbstractShapedCraftRecipe<C, S>, S extends CraftingRecipeSettings> extends CraftingRecipe<C, S> {

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

    protected AbstractShapedCraftRecipe(CraftingRecipe<?, S> craftingRecipe) {
        super(craftingRecipe);
        if (craftingRecipe instanceof AbstractShapedCraftRecipe<?, ?> shapedRecipe) {
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
     * This generates and stores the flipped states of the recipe shape.
     * We trade more calculation on start-up for better performance later on.
     */
    public class Shape {

        private final int width;
        private final int height;

        private final List<int[]> entries;

        /**
         * This constructor performs a very resource intensive calculation <br>
         * to generate, flip, and flatten the original shape <br>
         * to multiple int only arrays of different possible states of the recipe.
         */
        public Shape() {
            List<int[]> shapeEntryList = new ArrayList<>();
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
            this.entries = List.copyOf(shapeEntryList);
        }

        private void apply(List<int[]> entries, int[][] array) {
            var entry = Stream.of(array).flatMapToInt(IntStream::of).toArray();
            if (!entries.contains(entry)) {
                entries.add(entry);
            }
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public List<int[]> getUniqueShapes() {
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
