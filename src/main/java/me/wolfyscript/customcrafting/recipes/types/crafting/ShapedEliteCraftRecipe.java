package me.wolfyscript.customcrafting.recipes.types.crafting;

import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.AbstractShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public class ShapedEliteCraftRecipe extends AbstractShapedCraftRecipe<ShapedEliteCraftRecipe, EliteRecipeSettings> {

    public ShapedEliteCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 6);
        setSize();
    }

    public ShapedEliteCraftRecipe() {
        super(6);
        setSize();
    }

    public ShapedEliteCraftRecipe(ShapedEliteCraftRecipe eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
        setSize();
    }

    public ShapedEliteCraftRecipe(CraftingRecipe<?, EliteRecipeSettings> craftingRecipe) {
        super(craftingRecipe);
        setSize();
    }

    private void setSize() {
        this.requiredGridSize = 6;
        this.bookSquaredGrid = 36;
    }

    @Override
    public RecipeType<ShapedEliteCraftRecipe> getRecipeType() {
        return Types.ELITE_WORKBENCH_SHAPED;
    }

    @Override
    public ShapedEliteCraftRecipe clone() {
        return new ShapedEliteCraftRecipe(this);
    }
}