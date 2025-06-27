package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.scafall.config.jackson.RegistryKeyTypeIdResolver
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

/**
 * Modifies the result of a recipe using transformations.
 *
 */
interface ResultModifier {

    val transformations: List<Transformation>

    fun modify(recipeEvaluationResult: RecipeEvaluationResult<*, *>, result: ItemStack, context: EvaluationContext): ItemStack

    /**
     * Modifies the result using the data from the specified ingredients in the recipe.
     */
    interface Transformation {

        /**
         * The ingredient slots that are used to modify the result.
         */
        val ingredients: Array<Int>

        /**
         * The transmuter used to modify the result based on the specified ingredients.
         */
        val transmuter: Transmuter

        fun transform(recipeEvaluationResult: RecipeEvaluationResult<*, *>, result: ItemStack, context: EvaluationContext): ItemStack

        /**
         * Gets the data from the specified ingredients and modifies the result with it.
         * For example, merging data components from an ingredient into the resulting stack.
         */
        @JsonTypeIdResolver(RegistryKeyTypeIdResolver::class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type")
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        @JsonPropertyOrder(value = ["type"])
        interface Transmuter {

            fun mutate(transformation: Transformation, recipeEvaluationResult: RecipeEvaluationResult<*, *>, result: ItemStack, context: EvaluationContext): ItemStack

        }

    }

}