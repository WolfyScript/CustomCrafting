package com.wolfyscript.customcrafting.recipes

interface RecipeConditions {

    val conditions: List<Condition>

    fun areSatisfied(evaluationContext: EvaluationContext): Boolean

}

interface Condition {

    fun isSatisfied(evaluationContext: EvaluationContext): Boolean

}