package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public class CraftingRecipeEliteShapeless extends AbstractRecipeShapeless<CraftingRecipeEliteShapeless, EliteRecipeSettings> {

    public CraftingRecipeEliteShapeless(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 6, EliteRecipeSettings.class);
    }

    public CraftingRecipeEliteShapeless(NamespacedKey key) {
        super(key, 6, new EliteRecipeSettings());
    }

    public CraftingRecipeEliteShapeless(CraftingRecipeEliteShapeless eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
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
