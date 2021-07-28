package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public class CustomRecipeShapelessElite extends AbstractRecipeShapeless<CustomRecipeShapelessElite, EliteRecipeSettings> {

    public CustomRecipeShapelessElite(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 6);
    }

    public CustomRecipeShapelessElite() {
        super(6);
    }

    public CustomRecipeShapelessElite(CustomRecipeShapelessElite eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
    }

    public CustomRecipeShapelessElite(CraftingRecipe<?, EliteRecipeSettings> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<CustomRecipeShapelessElite> getRecipeType() {
        return Types.ELITE_WORKBENCH_SHAPELESS;
    }

    @Override
    public CustomRecipeShapelessElite clone() {
        return new CustomRecipeShapelessElite(this);
    }
}
