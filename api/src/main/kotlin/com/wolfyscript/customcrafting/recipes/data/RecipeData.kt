package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing
import com.wolfyscript.customcrafting.recipes.RecipeResult

/**
 * Holds information about the selected recipe after evaluation.
 *
 * Some types (like [RepairingRecipeData]) may expand it with type-specific data.
 */
interface RecipeData<T: CustomRecipe<*,*>> {

    /**
     * The selected recipe
     */
    val recipe: T

    /**
     * Gets the IngredientData at the specified slot **in the recipe**
     */
    fun bySlot(slot: Int): IngredientData?

    /**
     * Gets all the ingredients in order of appearance, skipping empty ingredients.
     */
    val nonNullIngredients: List<IngredientData>

    interface RepairingRecipeData : RecipeData<CustomRecipeRepairing> {

        var itemRepairCost: Int?

    }

}