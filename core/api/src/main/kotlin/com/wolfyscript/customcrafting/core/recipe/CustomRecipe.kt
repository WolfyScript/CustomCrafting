package com.wolfyscript.customcrafting.core.recipe

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver
import com.wolfyscript.customcrafting.core.recipe.condition.RecipeConditions
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput

/**
 * Represents a custom recipe that can be evaluated and executed within the CustomCrafting system.
 *
 * @param I The type of input required for this recipe
 * @param D The type of data that will be produced upon successful evaluation
 */
@JsonTypeResolver(RecipeTypeResolver::class)
@JsonTypeIdResolver(RecipeTypeIdResolver::class)
@JsonPropertyOrder("type")
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
interface CustomRecipe<I : RecipeInput, D : RecipeEvaluationResult.Data> {

    /**
     * The type of the recipe.
     */
    val type: RecipeType<*>

    /**
     * The priority of the recipe.
     * Recipes of higher priority are checked before recipes of lower priority.
     */
    val priority: Int

    /**
     * Conditions that must be met for the recipe to work.
     */
    val conditions: RecipeConditions

    /**
     * The group of the recipe.
     * Recipes with the same group are considered to be variants of each other.
     */
    val group: String

    /**
     * Evaluates the recipe using its input and the context and returns data that contains information about the evaluated recipe, like ingredients and their matched [com.wolfyscript.scafall.items.ItemStackRef]
     *
     * @return data about the evaluated recipe state; null if not valid for the given input and context.
     */
    fun evaluate(input: I, context: EvaluationContext): D?

}