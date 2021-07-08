package me.wolfyscript.customcrafting.recipes.data;

import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains all the data that is cached when a player crafts a recipe.
 * This way the recipe doesn't need to be verified again when the player collects the result.
 * <p>
 * It indexes at which place of the grid which CustomItem is used, so it can use the CustomItem consume options, etc. a user might have saved.
 * <p>
 * The indexed Ingredients are used to target specific items and is used inside the {@link me.wolfyscript.customcrafting.utils.recipe_item.target.MergeOption}s.
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
