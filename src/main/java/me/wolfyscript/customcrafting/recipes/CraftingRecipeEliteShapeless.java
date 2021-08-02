package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public class CraftingRecipeEliteShapeless extends AbstractRecipeShapeless<CraftingRecipeEliteShapeless, EliteRecipeSettings> {

    public CraftingRecipeEliteShapeless(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 6);
    }

    public CraftingRecipeEliteShapeless() {
        super(6);
    }

    public CraftingRecipeEliteShapeless(CraftingRecipeEliteShapeless eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
    }

    public CraftingRecipeEliteShapeless(CraftingRecipe<?, EliteRecipeSettings> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<CraftingRecipeEliteShapeless> getRecipeType() {
        return RecipeType.ELITE_WORKBENCH_SHAPELESS;
    }

    @Override
    public CraftingRecipeEliteShapeless clone() {
        return new CraftingRecipeEliteShapeless(this);
    }
}
