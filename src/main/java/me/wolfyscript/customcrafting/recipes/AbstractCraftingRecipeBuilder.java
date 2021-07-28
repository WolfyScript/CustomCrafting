package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.target.SlotResultTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractCraftingRecipeBuilder<R extends CraftingRecipe<R, S>, S extends CraftingRecipeSettings, B extends AbstractCraftingRecipeBuilder<R, S, B>> extends RecipeBuilder<R, SlotResultTarget, B> {

    private Map<Character, Ingredient> ingredients;
    private S settings;

    protected AbstractCraftingRecipeBuilder() {
        this.ingredients = new HashMap<>();
    }

    protected AbstractCraftingRecipeBuilder(R recipe) {
        super(recipe);
        this.ingredients = recipe.getIngredients();
    }

    public void setIngredients(Map<Character, Ingredient> ingredients) {
        this.ingredients = ingredients.entrySet().stream().filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, o2) -> o));
    }

    public void setIngredient(char key, Ingredient ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            this.ingredients.remove(key);
        } else {
            ingredients.buildChoices();
            this.ingredients.put(key, ingredients);
        }
    }

    public S getSettings() {
        return settings;
    }

    public void setSettings(S settings) {
        this.settings = settings;
    }
}
