package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
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

    @Override
    public void constructRecipe() {

    }

    public void addIngredients(Ingredient... ingredients) {
        addIngredients(Arrays.asList(ingredients));
    }

    public void addIngredients(List<Ingredient> ingredients) {
        Preconditions.checkArgument(this.ingredients.size() + ingredients.size() <= bookSquaredGrid, "Recipe cannot have more than " + bookSquaredGrid + " ingredients!");
        List<Ingredient> currentIngredients = new ArrayList<>(this.ingredients);
        currentIngredients.addAll(ingredients);
        setIngredients(currentIngredients);
    }

    public void addIngredient(int count, Ingredient ingredient) {
        Preconditions.checkArgument(ingredients.size() + count <= bookSquaredGrid, "Recipe cannot have more than " + bookSquaredGrid + " ingredients!");
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
    }

    @Override
    public boolean fitsDimensions(CraftManager.MatrixData matrixData) {
        return ingredients.size() == matrixData.getStrippedSize();
    }

    @Override
    public CraftingData check(CraftManager.MatrixData matrixData) {
        List<Integer> usedKeys = new ArrayList<>();
        Map<Integer, IngredientData> dataMap = new HashMap<>();
        ItemStack[] matrix = matrixData.getMatrix();
        for (int i = 0; i < matrix.length; i++) {
            checkIngredient(i, usedKeys, dataMap, matrix[i]);
        }
        return usedKeys.size() == ingredients.size() ? new CraftingData(this, dataMap) : null;
    }

    protected void checkIngredient(int pos, List<Integer> usedKeys, Map<Integer, IngredientData> dataMap, ItemStack item) {
        if (item == null) return;
        for (int i = 0; i < ingredients.size(); i++) {
            if (usedKeys.contains(i)) continue;
            Optional<CustomItem> validItem = ingredients.get(i).check(item, isExactMeta());
            if (validItem.isPresent()) {
                usedKeys.add(i);
                var customItem = validItem.get().clone();
                if (customItem != null) {
                    dataMap.put(pos, new IngredientData(i, ingredients.get(i), customItem, item));
                }
                return;
            }
        }
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
