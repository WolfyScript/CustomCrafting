package com.wolfyscript.customcrafting.recipes

class RecipeConditionsImpl(override val conditions: List<Condition>) : RecipeConditions {

    override fun areSatisfied(evaluationContext: EvaluationContext): Boolean {
        return conditions.all { it.isSatisfied(evaluationContext) }
    }

    override fun toString(): String {
        return "RecipeConditionsImpl(conditions=$conditions)"
    }

}
