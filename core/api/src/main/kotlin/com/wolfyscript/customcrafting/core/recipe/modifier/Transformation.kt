package com.wolfyscript.customcrafting.core.recipe.modifier

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult
import com.wolfyscript.scafall.config.jackson.RegistryKeyTypeIdResolver
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

/**
 * Modifies the result using the data from the specified ingredients in the recipe.
 */
interface Transformation {

    companion object {
        fun of(ingredients: Array<Int>, transmuter: Transmuter) : Transformation =
            TransformationImpl(ingredients, transmuter)
    }

    /**
     * The ingredient slots that are used to modify the target.
     */
    val ingredients: Array<Int>

    /**
     * The transmuter used to modify the target based on the specified ingredients.
     */
    val transmuter: Transmuter

    /**
     * Transforms the target item using the specified ingredients and transmuter.
     */
    fun transform(target: ScafallItemStack, evalResult: RecipeEvaluationResult<*, *>, context: EvaluationContext): ScafallItemStack

    /**
     * Gets the data from the specified ingredients and modifies the target item with it.
     * For example, copying data components from an ingredient onto the target stack.
     *
     * This Transmuter is part of a [Registry][com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes.recipeItemTransmuters], which can be expanded with custom transmuter implementations by third-parties.
     */
    @JsonTypeIdResolver(RegistryKeyTypeIdResolver::class)
    @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type")
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @JsonPropertyOrder(value = ["type"])
    interface Transmuter {

        /**
         * Mutates the given target item using the specified transformation data.
         */
        fun mutate(target: ScafallItemStack, transformation: Transformation, evalResult: RecipeEvaluationResult<*, *>, context: EvaluationContext): ScafallItemStack

    }

}