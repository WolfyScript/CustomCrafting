package com.wolfyscript.customcrafting.core.recipe.condition

import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext

/**
 * A collection of [Conditions][Condition] which checks if all of them are satisfied.
 */
interface RecipeConditions {

    companion object {

        fun of(conditions: List<Condition> = emptyList()): RecipeConditions =
            RecipeConditionsImpl(conditions)

    }

    val conditions: List<Condition>

    /**
     * Checks if all the [conditions] are satisfied by the specified [evaluationContext].
     */
    fun areSatisfied(evaluationContext: EvaluationContext): Boolean

}

