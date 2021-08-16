package me.wolfyscript.customcrafting.recipes.data;

import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.recipe_item.target.MergeOption;

import java.util.Map;

/**
 * Besides the usual use case for {@link MergeOption}s,
 * this data is also used internally when a player takes out the result so the recipe doesn't need to be verified again.
 * <br>
 * Additionally, it saves the position of the IngredientData inside the crafting grid.
 */
public class CraftingData extends RecipeData<CraftingRecipe<?, ?>> {

    public CraftingData(CraftingRecipe<?, ?> recipe, Map<Integer, IngredientData> ingredients) {
        super(recipe, ingredients);
    }

}
