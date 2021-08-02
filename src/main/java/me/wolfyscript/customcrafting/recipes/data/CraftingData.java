package me.wolfyscript.customcrafting.recipes.data;

import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Besides the usual use case for {@link me.wolfyscript.customcrafting.utils.recipe_item.target.MergeOption}s,
 * this data is also used internally when a player takes out the result so the recipe doesn't need to be verified again.
 * <br>
 * Additionally, it saves the position of the IngredientData inside the crafting grid.
 */
public class CraftingData extends RecipeData<CraftingRecipe<?, ?>> {

    public CraftingData(CraftingRecipe<?, ?> recipe, Map<Integer, IngredientData> ingredients) {
        super(recipe, ingredients);
    }

    /**
     * @return the CustomItems found per slot.
     * @deprecated Iterate over the entries of {@link #getIngredients()} directly!
     */
    @Deprecated
    public Map<Vec2d, CustomItem> getFoundItems() {
        return new HashMap<>();
    }

    /**
     * @return A Map of the Ingredients with the position inside the crafting grid.
     */
    @Deprecated
    public Map<Vec2d, IngredientData> getIngredients() {
        return new HashMap<>();
    }


}
