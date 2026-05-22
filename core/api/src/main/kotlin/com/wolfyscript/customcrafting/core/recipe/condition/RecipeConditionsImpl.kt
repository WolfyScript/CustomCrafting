package com.wolfyscript.customcrafting.core.recipe.condition

import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext

class RecipeConditionsImpl(override val conditions: List<Condition> = emptyList()) : RecipeConditions {

    override fun areSatisfied(evaluationContext: EvaluationContext): Boolean {
        return conditions.all { it.isSatisfied(evaluationContext) }
    }

    override fun toString(): String {
        return "$conditions"
    }

}