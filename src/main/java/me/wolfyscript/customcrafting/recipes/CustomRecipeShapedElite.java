package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public class CustomRecipeShapedElite extends AbstractRecipeShaped<CustomRecipeShapedElite, EliteRecipeSettings> {

    public CustomRecipeShapedElite(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 6);
    }

    public CustomRecipeShapedElite() {
        super(6);
    }

    public CustomRecipeShapedElite(CustomRecipeShapedElite eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
    }

    public CustomRecipeShapedElite(CraftingRecipe<?, EliteRecipeSettings> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<CustomRecipeShapedElite> getRecipeType() {
        return Types.ELITE_WORKBENCH_SHAPED;
    }

    @Override
    public CustomRecipeShapedElite clone() {
        return new CustomRecipeShapedElite(this);
    }
}