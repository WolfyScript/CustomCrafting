package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public class CraftingRecipeEliteShaped extends AbstractRecipeShaped<CraftingRecipeEliteShaped, EliteRecipeSettings> {

    public CraftingRecipeEliteShaped(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 6, EliteRecipeSettings.class);
    }

    public CraftingRecipeEliteShaped() {
        super(6, new EliteRecipeSettings());
    }

    public CraftingRecipeEliteShaped(CraftingRecipeEliteShaped eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
    }

    public CraftingRecipeEliteShaped(CraftingRecipe<?, EliteRecipeSettings> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<CraftingRecipeEliteShaped> getRecipeType() {
        return RecipeType.ELITE_WORKBENCH_SHAPED;
    }

    @Override
    public CraftingRecipeEliteShaped clone() {
        return new CraftingRecipeEliteShaped(this);
    }
}