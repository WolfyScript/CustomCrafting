package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.RecipePacketType;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.AbstractShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public class ShapedEliteCraftRecipe extends AbstractShapedCraftRecipe<ShapedEliteCraftRecipe> implements EliteCraftingRecipe {

    public ShapedEliteCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        setSize();
    }

    public ShapedEliteCraftRecipe() {
        super();
        setSize();
    }

    public ShapedEliteCraftRecipe(ShapedEliteCraftRecipe eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
        setSize();
    }

    public ShapedEliteCraftRecipe(CraftingRecipe<?> craftingRecipe) {
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
    public RecipePacketType getPacketType() {
        return RecipePacketType.ELITE_CRAFTING_SHAPED;
    }

    @Override
    public ShapedEliteCraftRecipe clone() {
        return new ShapedEliteCraftRecipe(this);
    }
}