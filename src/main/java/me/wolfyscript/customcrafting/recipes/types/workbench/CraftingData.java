package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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
public class CraftingData {

    private final CraftingRecipe<?> recipe;
    private final Map<Vec2d, IngredientData> ingredients;
    private final Map<Integer, IngredientData> indexedBySlot;
    private Result<?> result;

    public CraftingData(CraftingRecipe<?> recipe, Map<Vec2d, IngredientData> ingredients) {
        this.recipe = recipe;
        this.ingredients = ingredients;
        this.result = recipe.getResult();
        this.indexedBySlot = ingredients.values().stream().collect(Collectors.toMap(IngredientData::recipeSlot, data -> data));
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

    /**
     * @return A Map of the Ingredients with the position inside the crafting grid.
     */
    public Map<Vec2d, IngredientData> getIngredients() {
        return ingredients;
    }

    /**
     * The slots indicate the index (position) of the Ingredient inside the recipe.
     * For normal recipes that means from 0 - 9.
     * For Elite recipes the range is from 0 - 36.
     * <p>
     * For the correct position open the in-game Recipe Creator GUI and see in which slot the ingredient is.
     * You may take the character saved in the config and use the index of it inside this String "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
     *
     * @param slot The recipe slot to get the {@link IngredientData} for.
     * @return The {@link IngredientData} of the specified recipe slot.
     */
    @Nullable
    public IngredientData getBySlot(int slot) {
        return indexedBySlot.get(slot);
    }

    /**
     * The slots indicate the index (position) of the Ingredient inside the recipe.
     * For normal recipes that means from 0 - 9.
     * For Elite recipes the range is from 0 - 36.
     * <p>
     * For the correct position open the in-game Recipe Creator GUI and see in which slot the ingredient is.
     * You may take the character saved in the config and use the index of it inside this String "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
     *
     * @param slots The recipe slots to get the {@link IngredientData} for.
     * @return A list of {@link IngredientData} of the specified recipe slots.
     */
    public List<IngredientData> getBySlots(int[] slots) {
        List<IngredientData> list = new ArrayList<>();
        for (int slot : slots) {
            list.add(getBySlot(slot));
        }
        return list;
    }
}
