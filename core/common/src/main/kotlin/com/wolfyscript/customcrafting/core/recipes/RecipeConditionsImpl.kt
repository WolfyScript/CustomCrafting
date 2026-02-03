package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.recipes.conditions.Condition
import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions

class RecipeConditionsImpl(override val conditions: List<Condition> = emptyList()) : RecipeConditions {

    override fun areSatisfied(evaluationContext: EvaluationContext): Boolean {
        return conditions.all { it.isSatisfied(evaluationContext) }
    }

    override fun toString(): String {
        return "$conditions"
    }

}
