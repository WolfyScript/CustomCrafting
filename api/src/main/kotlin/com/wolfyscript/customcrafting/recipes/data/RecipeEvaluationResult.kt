package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.CustomRecipeRepairing
import com.wolfyscript.customcrafting.recipes.RecipeReference

/**
 * Holds information about the selected recipe after evaluation.
 *
 * Some types (like [RepairingRecipeData]) may expand it with type-specific data.
 */
interface RecipeEvaluationResult<D: RecipeEvaluationResult.Data, T: CustomRecipe<*,*>> {

    /**
     * The selected recipe
     */
    val recipe: RecipeReference<T>

    val data: D

    /**
     * Used to store type-specific recipe data after evaluation
     */
    interface Data {

        /**
         * Gets the IngredientData at the specified slot **in the recipe**
         */
        fun bySlot(slot: Int): IngredientData?

        /**
         * Gets all the ingredients in order of appearance, skipping empty ingredients.
         */
        val nonNullIngredients: List<IngredientData>

    }

    interface RepairingRecipeData : Data {

        var itemRepairCost: Int?

    }

}