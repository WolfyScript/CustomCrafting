package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;

public abstract class EliteCraftingRecipe extends CraftingRecipe<EliteCraftingRecipe> {

    protected int requiredGridSize;

    public EliteCraftingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.bookGridSize = 6;
        this.bookSquaredGrid = 36;
    }

    public EliteCraftingRecipe() {
        super();
        this.bookGridSize = 6;
        this.bookSquaredGrid = 36;
    }

    public EliteCraftingRecipe(EliteCraftingRecipe eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
        this.bookGridSize = 6;
        this.bookSquaredGrid = 36;
    }

    public int getRequiredGridSize() {
        return requiredGridSize;
    }

    @Override
    public RecipeType<EliteCraftingRecipe> getRecipeType() {
        return Types.ELITE_WORKBENCH;
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
    }
}
