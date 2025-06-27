package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver
import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonTypeResolver(RecipeTypeResolver::class)
@JsonTypeIdResolver(RecipeTypeIdResolver::class)
@JsonPropertyOrder("type")
interface CustomRecipe<I: RecipeInput, D: RecipeEvaluationResult.Data> {

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
     * Evaluates the recipe using its input and the context and returns data that contains information about the evaluated recipe, like ingredients and their matched [com.wolfyscript.scafall.items.ItemStackRef]
     *
     * @return data about the evaluated recipe state; null if not valid for the given input and context.
     */
    fun evaluate(input: I, context: EvaluationContext): D?

}