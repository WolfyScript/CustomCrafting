package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.RecipeUtil;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class AbstractRecipeShaped<C extends AbstractRecipeShaped<C, S>, S extends CraftingRecipeSettings<S>> extends CraftingRecipe<C, S> {

    protected Map<Character, Ingredient> mappedIngredients;
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
        this.mappedIngredients = Map.of();

        Map<Character, Ingredient> loadedIngredients = Streams.stream(node.path(INGREDIENTS_KEY).fields()).collect(Collectors.toMap(entry -> entry.getKey().charAt(0), entry -> ItemLoader.loadIngredient(entry.getValue())));
        if (node.has("shape")) {
            setShape(mapper.convertValue(node.path("shape"), String[].class));
        } else {
            generateMissingShape(List.copyOf(loadedIngredients.keySet()));
        }
        setIngredients(loadedIngredients);
    }

    protected AbstractRecipeShaped(NamespacedKey key, int gridSize, S settings) {
        super(key, gridSize, settings);
        this.mirrorHorizontal = true;
        this.mirrorVertical = false;
        this.mirrorRotation = false;
        this.mappedIngredients = new HashMap<>();
    }

    protected AbstractRecipeShaped(AbstractRecipeShaped<C, S> recipe) {
        super(recipe);
        this.mirrorHorizontal = recipe.mirrorHorizontal;
        this.mirrorVertical = recipe.mirrorVertical;
        this.mirrorRotation = recipe.mirrorRotation;
        this.mappedIngredients = new HashMap<>();
        setShape(recipe.shape.clone());
        setIngredients(recipe.mappedIngredients.entrySet().stream().map(entry -> Map.entry(entry.getKey(), entry.getValue().clone())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
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

    public String[] getShape() {
        return shape;
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
    public void setShape(@NotNull String... shape) {
        Preconditions.checkArgument(shape != null && shape.length > 0, "Shape can not be null!");
        Preconditions.checkArgument(shape.length <= requiredGridSize, "Shape must not have more than " + requiredGridSize + " rows!");
        int currentWidth = -1;
        for (String row : shape) {
            Preconditions.checkArgument(Objects.requireNonNull(row, "Shape row cannot be null!").length() <= requiredGridSize, "Shape row must not be longer than " + requiredGridSize + "!");
            Preconditions.checkArgument(currentWidth == -1 || currentWidth == row.length(), "Shape must be rectangular!");
            currentWidth = row.length();
        }
        this.shape = RecipeUtil.formatShape(shape).toArray(new String[0]);
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
        var genShape = new String[requiredGridSize];
        var index = 0;
        var row = 0;
        for (int i = 0; i < bookSquaredGrid; i++) {
            var ingrd = ICraftingRecipe.LETTERS.charAt(i);
            final var current = genShape[row] != null ? genShape[row] : "";
            if (!keys.contains(ingrd)) {
                genShape[row] = current + " ";
            } else {
                genShape[row] = current + ingrd;
            }
            index++;
            if ((index % requiredGridSize) == 0) {
                row++;
            }
        }
        setShape(genShape);
    }

    @Override
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

    public void setIngredients(Map<Character, Ingredient> ingredients) {
        this.mappedIngredients = ingredients.entrySet().stream().filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Preconditions.checkArgument(!this.mappedIngredients.isEmpty(), "Invalid ingredients! Recipe must have non-air ingredients!");
        createFlatIngredients();
    }

    /**
     * @return An unmodifiable Map copy of the Ingredients mapped to the character in the shape.
     */
    public Map<Character, Ingredient> getMappedIngredients() {
        return Map.copyOf(mappedIngredients);
    }

    @Override
    public boolean fitsDimensions(@NotNull CraftManager.MatrixData matrixData) {
        return ingredients.size() == matrixData.getMatrix().length && internalShape.height == matrixData.getHeight() && internalShape.width == matrixData.getWidth();
    }

    @Override
    public CraftingData check(CraftManager.MatrixData matrixData) {
        for (int[] entry : internalShape.getUniqueShapes()) {
            var craftingData = checkShape(matrixData, entry);
            if (craftingData != null) return craftingData;
        }
        return null;
    }

    protected CraftingData checkShape(@NotNull CraftManager.MatrixData matrixData, int[] shape) {
        Map<Integer, IngredientData> dataMap = new HashMap<>();
        var i = 0;
        for (ItemStack invItem : matrixData.getMatrix()) {
            int slot = shape[i];
            if (invItem != null) {
                if (slot >= 0) {
                    var ingredient = ingredients.get(slot);
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
        gen.writeObjectField(INGREDIENTS_KEY, this.mappedIngredients);
    }

    @Override
    public void writeToBuf(MCByteBuf byteBuf) {
        super.writeToBuf(byteBuf);
        byteBuf.writeVarInt(shape.length);
        for (String s : shape) {
            byteBuf.writeUtf(s, 3);
        }
        byteBuf.writeVarInt(mappedIngredients.size());
        mappedIngredients.forEach((key, ingredient) -> {
            byteBuf.writeInt(LETTERS.indexOf(key));
            byteBuf.writeVarInt(ingredient.size());
            for (CustomItem choice : ingredient.getChoices()) {
                byteBuf.writeItemStack(choice.create());
            }
        });
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
