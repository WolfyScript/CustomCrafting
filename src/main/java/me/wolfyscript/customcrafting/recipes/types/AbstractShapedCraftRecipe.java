package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
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

public abstract class AbstractShapedCraftRecipe<C extends AbstractShapedCraftRecipe<C>> extends CraftingRecipe<C> {

    private String[] shape;
    private Shape internalShape;
    private boolean mirrorHorizontal;
    private boolean mirrorVertical;
    private boolean mirrorRotation;

    protected AbstractShapedCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        constructShape();
        JsonNode mirrorNode = node.path("mirror");
        this.mirrorHorizontal = mirrorNode.path("horizontal").asBoolean(true);
        this.mirrorVertical = mirrorNode.path("vertical").asBoolean(false);
        this.mirrorRotation = mirrorNode.path("rotation").asBoolean(false);
    }

    protected AbstractShapedCraftRecipe() {
        super();
        this.mirrorHorizontal = true;
        this.mirrorVertical = false;
        this.mirrorRotation = false;
    }

    protected AbstractShapedCraftRecipe(CraftingRecipe<?> craftingRecipe) {
        super(craftingRecipe);
        constructShape();
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
    public void constructShape() {
        if (this.shape == null) {
            this.shape = generateMissingShape();
        }
        //Create flatten ingredients. This makes it possible to use a key multiple times in one shape.
        var flattenShape = String.join("", shape);
        this.ingredientsFlat = new ArrayList<>();
        for (char key : flattenShape.toCharArray()) {
            ingredientsFlat.add(getIngredients().getOrDefault(key, new Ingredient()));
        }
        //Create internal shape, which is more performant when used in checks later on.
        this.internalShape = new AbstractShapedCraftRecipe.Shape(shape, getIngredients());

        shape = RecipeUtil.formatShape(shape).toArray(new String[0]);
    }

    private String[] generateMissingShape() {
        String[] shape = new String[3];
        int index = 0;
        int row = 0;
        for (int i = 0; i < 9; i++) {
            var ingrd = ICraftingRecipe.LETTERS.charAt(i);
            var items = getIngredients().get(ingrd);
            final var current = shape[row] != null ? shape[row] : "";
            if (items == null || items.isEmpty()) {
                shape[row] = current + " ";
            } else {
                shape[row] = current + ingrd;
            }
            index++;
            if ((index % 3) == 0) {
                row++;
            }
        }
        return shape;
    }

    @Override
    public boolean isShapeless() {
        return false;
    }

    @Override
    public CraftingData check(List<List<ItemStack>> ingredients) {
        var craftingData = checkShape(ingredients, internalShape.shape);
        if (craftingData == null) {
            if (mirrorHorizontal()) {
                craftingData = checkShape(ingredients, internalShape.flippedHorizontally);
                if (craftingData != null) {
                    return craftingData;
                }
            }
            if (mirrorVertical()) {
                craftingData = checkShape(ingredients, internalShape.flippedVertically);
                if (craftingData != null) {
                    return craftingData;
                }
            }
            if (mirrorHorizontal() && mirrorVertical() && mirrorRotation()) {
                craftingData = checkShape(ingredients, internalShape.rotated);
                return craftingData;
            }
        }
        return craftingData;
    }

    protected CraftingData checkShape(List<List<ItemStack>> matrix, int[][] internalShape) {
        if (ingredientsFlat == null || ingredientsFlat.isEmpty() || matrix.size() != internalShape.length || matrix.get(0).size() != internalShape[0].length) {
            return null;
        }
        Map<Vec2d, IngredientData> dataMap = new HashMap<>(); //TODO: A more efficient store of ingredients. For example in a list and index ingredients with integers!
        for (var column = 0; column < matrix.size(); column++) {
            for (var row = 0; row < matrix.get(column).size(); row++) {
                ItemStack targetItem = matrix.get(column).get(row);
                if (targetItem != null) {
                    int slot = internalShape[column][row];
                    if (slot >= 0) {
                        var ingredient = ingredientsFlat.get(slot);
                        if (ingredient != null) {
                            Optional<CustomItem> item = ingredient.check(targetItem, this.exactMeta);
                            if (item.isPresent()) {
                                dataMap.put(new Vec2d(row, column), new IngredientData(slot, ingredient, item.get(), targetItem));
                                continue;
                            }
                        }
                    }
                    return null;
                } else if (internalShape[column][row] <= -1) {
                    return null;
                }
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

    public static class Shape {

        private final int[][] shape;
        private final int[][] flippedVertically;
        private final int[][] flippedHorizontally;
        private final int[][] rotated;

        public Shape(String[] originalShape, Map<Character, Ingredient> ingredients) {
            //Original shape
            this.shape = new int[originalShape.length][originalShape[0].length()];
            int index = 0;
            for (int i = 0; i < originalShape.length; i++) {
                for (int j = 0; j < originalShape[i].length(); j++) {
                    shape[i][j] = originalShape[i].charAt(j) != ' ' ? index++ : -1;
                }
            }

            this.flippedVertically = this.shape.clone();
            ArrayUtils.reverse(this.flippedVertically);

            this.flippedHorizontally = this.shape.clone();
            for (int[] ints : this.flippedHorizontally) {
                ArrayUtils.reverse(ints);
            }

            this.rotated = this.flippedVertically.clone();
            for (int[] ints : this.rotated) {
                ArrayUtils.reverse(ints);
            }
        }

        public int[][] getOriginal() {
            return shape;
        }

        public int[][] getFlippedVertically() {
            return flippedVertically;
        }

        public int[][] getFlippedHorizontally() {
            return flippedHorizontally;
        }

        public int[][] getRotated() {
            return rotated;
        }
    }

}
