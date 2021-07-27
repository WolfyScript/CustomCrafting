package me.wolfyscript.customcrafting.recipes.types.crafting;

import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.AbstractShapelessCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public class ShapelessEliteCraftRecipe extends AbstractShapelessCraftingRecipe<ShapelessEliteCraftRecipe, EliteRecipeSettings> {

    public ShapelessEliteCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 6);
    }

    public ShapelessEliteCraftRecipe() {
        super(6);
    }

    public ShapelessEliteCraftRecipe(ShapelessEliteCraftRecipe eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
    }

    public ShapelessEliteCraftRecipe(CraftingRecipe<?, EliteRecipeSettings> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<ShapelessEliteCraftRecipe> getRecipeType() {
        return Types.ELITE_WORKBENCH_SHAPELESS;
    }

    @Override
    public ShapelessEliteCraftRecipe clone() {
        return new ShapelessEliteCraftRecipe(this);
    }
}
