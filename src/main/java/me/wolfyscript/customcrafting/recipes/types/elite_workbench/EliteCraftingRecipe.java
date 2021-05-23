package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public abstract class EliteCraftingRecipe extends CraftingRecipe<EliteCraftingRecipe> {

    protected EliteCraftingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.bookGridSize = 6;
        this.bookSquaredGrid = 36;
    }

    protected EliteCraftingRecipe() {
        super();
        this.bookGridSize = 6;
        this.bookSquaredGrid = 36;
    }

    protected EliteCraftingRecipe(EliteCraftingRecipe eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
        this.bookGridSize = 6;
        this.bookSquaredGrid = 36;
    }

    @Override
    public RecipeType<EliteCraftingRecipe> getRecipeType() {
        return Types.ELITE_WORKBENCH;
    }
}
