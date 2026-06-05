package com.wolfyscript.customcrafting.core.recipe.evaluation

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.wolfyscript.customcrafting.core.recipe.CustomRecipe
import com.wolfyscript.customcrafting.core.recipe.RecipeReference

/**
 * Holds information about the selected recipe after evaluation.
 *
 * Some types (like [RepairingRecipeData]) may expand it with type-specific data.
 */
@JsonDeserialize(`as` = RecipeEvaluationResultImpl::class)
interface RecipeEvaluationResult<D: RecipeEvaluationResult.Data, T: CustomRecipe<*, *>> {

    companion object {

        fun <D: Data, T: CustomRecipe<*, *>> of(
            recipe: RecipeReference<T>,
            data: D
        ): RecipeEvaluationResult<D, T> {
            return RecipeEvaluationResultImpl(recipe, data)
        }

    }

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

    interface GrindingRecipeData : Data {

        var penalty: Int

        var yield: Int

    }

}