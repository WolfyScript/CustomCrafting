package com.wolfyscript.customcrafting.core.recipes.conditions

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.wolfyscript.customcrafting.core.recipes.EvaluationContext
import com.wolfyscript.scafall.config.jackson.RegistryKeyTypeIdResolver

/**
 * A condition that determines if it is satisfied based on the [com.wolfyscript.customcrafting.core.recipes.EvaluationContext].
 *
 * Types of Conditions are available in [recipeConditionTypes][com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes.recipeConditionTypes].
 * Custom Condition types can be registered too from third-parties. The condition type is defined by the "type" property in the config file.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type")
@JsonTypeIdResolver(RegistryKeyTypeIdResolver::class)
@JsonPropertyOrder("type")
interface Condition {

    fun isSatisfied(evaluationContext: EvaluationContext): Boolean

}