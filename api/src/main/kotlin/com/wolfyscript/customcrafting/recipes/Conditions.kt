package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver
import com.wolfyscript.scafall.config.jackson.KeyedTypeIdResolver
import com.wolfyscript.scafall.config.jackson.KeyedTypeResolver

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

/**
 * A condition that determines if it is satisfied based on the [EvaluationContext].
 *
 * Types of Conditions are available in [CCBuiltInRegistries.recipeConditionTypes][com.wolfyscript.customcrafting.registry.CCBuiltInRegistries.recipeConditionTypes].
 * Custom Condition types can be registered too from third-parties. The condition type is defined by the "type" property in the config file.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type")
@JsonTypeResolver(KeyedTypeResolver::class)
@JsonTypeIdResolver(KeyedTypeIdResolver::class)
@JsonPropertyOrder("type")
interface Condition {

    fun isSatisfied(evaluationContext: EvaluationContext): Boolean

}