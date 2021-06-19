package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains all the data that is cached when a player crafts a recipe.
 * This way the recipe doesn't need to be verified again when the player collects the result.
 * <p>
 * It indexes at which place of the grid which CustomItem is used, so it can use the CustomItem consume options, etc. a user might have saved.
 * <p>
 * The indexed Ingredients are used to target specific items and use it inside the Target options.
 */
public class CraftingData {

    CraftingRecipe<?> recipe;
    Result<?> result;
    Map<Vec2d, IngredientData> ingredients;

    public CraftingData(CraftingRecipe<?> recipe, Map<Vec2d, IngredientData> ingredients) {
        this.recipe = recipe;
        this.ingredients = ingredients;
        this.result = recipe.getResult();
    }

    public CraftingRecipe<?> getRecipe() {
        return recipe;
    }

    /**
     * @return the CustomItems found per slot.
     * @deprecated Iterate over the entries of {@link #getIngredients()} directly!
     */
    @Deprecated
    public Map<Vec2d, CustomItem> getFoundItems() {
        return ingredients.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, val -> val.getValue().customItem()));
    }

    public Result<?> getResult() {
        return result;
    }

    public void setResult(Result<?> result) {
        this.result = result;
    }

    public Map<Vec2d, IngredientData> getIngredients() {
        return ingredients;
    }

}
