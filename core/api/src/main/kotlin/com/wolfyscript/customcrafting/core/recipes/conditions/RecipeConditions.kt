package com.wolfyscript.customcrafting.core.recipes.conditions

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.recipes.EvaluationContext

/**
 * A collection of [Conditions][Condition] which checks if all of them are satisfied.
 */
interface RecipeConditions {

    companion object {

        fun of(conditions: List<Condition> = emptyList()): RecipeConditions =
            CustomCraftingProvider.get().factories.recipeFactory.createRecipeConditions(conditions)

    }

    val conditions: List<Condition>

    /**
     * Checks if all the [conditions] are satisfied by the specified [evaluationContext].
     */
    fun areSatisfied(evaluationContext: EvaluationContext): Boolean

}

