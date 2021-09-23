package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
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
        List<Integer> keys = new ArrayList<>(this.indexes);
        Map<Integer, IngredientData> dataMap = new HashMap<>();
        ItemStack[] matrix = matrixData.getMatrix();
        for (int i = 0; i < matrix.length; i++) {
            var key = checkIngredient(i, keys, dataMap, matrix[i]);
            if(key >= 0) {
                keys.remove(key);
            }
        }
        if(keys.isEmpty()) {
            return new CraftingData(this, dataMap);
        } else if (hasAllowedEmptyIngredient && keys.stream().allMatch(index -> ingredients.get(index).isAllowEmpty())) {
            int maxSize = ingredients.size() - keys.size();
            if (matrixData.getStrippedSize() == maxSize) {
                return new CraftingData(this, dataMap);
            }
        }
        return null;
    }

    protected Integer checkIngredient(int pos, List<Integer> keys, Map<Integer, IngredientData> dataMap, ItemStack item) {
        if (item != null) {
            for (Integer key : keys) {
                var ingredient = ingredients.get(key);
                Optional<CustomItem> validItem = ingredient.check(item, isExactMeta());
                if (validItem.isPresent()) {
                    dataMap.put(pos, new IngredientData(key, ingredient, validItem.get(), item));
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
