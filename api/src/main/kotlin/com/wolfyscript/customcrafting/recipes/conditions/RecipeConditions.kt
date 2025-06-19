package com.wolfyscript.customcrafting.recipes.conditions

import com.wolfyscript.customcrafting.recipes.EvaluationContext

/**
 * A collection of [Conditions][Condition] which checks if all of them are satisfied.
 */
interface RecipeConditions {

    val conditions: List<Condition>

    /**
     * Checks if all the [conditions] are satisfied by the specified [evaluationContext].
     */
    fun areSatisfied(evaluationContext: EvaluationContext): Boolean

}

