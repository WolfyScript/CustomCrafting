package me.wolfyscript.customcrafting.recipes.data;

import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Besides the usual use case for {@link me.wolfyscript.customcrafting.utils.recipe_item.target.MergeOption}s,
 * this data is also used internally when a player takes out the result so the recipe doesn't need to be verified again.
 * <br>
 * Additionally, it saves the position of the IngredientData inside the crafting grid.
 */
public class CraftingData extends RecipeData<CraftingRecipe<?>> {

    private final Map<Vec2d, IngredientData> ingredients;

    public CraftingData(CraftingRecipe<?> recipe, Map<Vec2d, IngredientData> ingredients) {
        super(recipe, ingredients.values().stream().collect(Collectors.toMap(IngredientData::recipeSlot, data -> data)));
        this.ingredients = ingredients;
    }

    /**
     * @return the CustomItems found per slot.
     * @deprecated Iterate over the entries of {@link #getIngredients()} directly!
     */
    @Deprecated
    public Map<Vec2d, CustomItem> getFoundItems() {
        return ingredients.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, val -> val.getValue().customItem()));
    }

    /**
     * @return A Map of the Ingredients with the position inside the crafting grid.
     */
    public Map<Vec2d, IngredientData> getIngredients() {
        return ingredients;
    }
}
