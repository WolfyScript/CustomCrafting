package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeResult

/**
 * Holds information about the selected recipe after evaluation.
 */
interface RecipeData<T: CustomRecipe> {

    /**
     * The selected recipe
     */
    val recipe: T

    /**
     * The result of the recipe
     */
    var result: RecipeResult

    /**
     * Gets the IngredientData at the specified slot **in the recipe**
     */
    fun bySlot(slot: Int): IngredientData?

    /**
     * Gets all the ingredients in order of appearance, skipping empty ingredients.
     */
    val nonNullIngredients: List<IngredientData>

}