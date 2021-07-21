package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.RecipePacketType;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.AbstractShapelessCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public class ShapelessEliteCraftRecipe extends AbstractShapelessCraftingRecipe<ShapelessEliteCraftRecipe> implements EliteCraftingRecipe {

    public ShapelessEliteCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 6);
    }

    public ShapelessEliteCraftRecipe() {
        super(6);
    }

    public ShapelessEliteCraftRecipe(ShapelessEliteCraftRecipe eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
    }

    public ShapelessEliteCraftRecipe(CraftingRecipe<?> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<ShapelessEliteCraftRecipe> getRecipeType() {
        return Types.ELITE_WORKBENCH_SHAPELESS;
    }

    @Override
    public RecipePacketType getPacketType() {
        return RecipePacketType.ELITE_CRAFTING_SHAPELESS;
    }

    @Override
    public ShapelessEliteCraftRecipe clone() {
        return new ShapelessEliteCraftRecipe(this);
    }
}
