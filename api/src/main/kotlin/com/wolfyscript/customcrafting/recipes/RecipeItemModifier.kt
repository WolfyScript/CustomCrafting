package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.scafall.config.jackson.RegistryKeyTypeIdResolver
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

/**
 * Modifies a target item with transformations using information about the recipe ingredients.
 */
interface RecipeItemModifier {

    /**
     * The transformations that are applied to the target item.
     */
    val transformations: List<Transformation>

    /**
     * Modifies the target item using the specified transformations.
     *
     * @param target the target item to modify.
     * @param evalResult the result of the recipe evaluation (ingredient info like which ingredient is present in each slot).
     * @param context the evaluation context (e.g. player, tile entity, etc.).
     *
     * @return the modified target item.
     */
    fun modify(target: ItemStack, evalResult: RecipeEvaluationResult<*, *>, context: EvaluationContext): ItemStack

    /**
     * Modifies the result using the data from the specified ingredients in the recipe.
     */
    interface Transformation {

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
        fun transform(target: ItemStack, evalResult: RecipeEvaluationResult<*, *>, context: EvaluationContext): ItemStack

        /**
         * Gets the data from the specified ingredients and modifies the target item with it.
         * For example, copying data components from an ingredient onto the target stack.
         *
         * This Transmuter is part of a [Registry][com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes.recipeItemTransmuters], which can be expanded with custom transmuter implementations by third-parties.
         */
        @JsonTypeIdResolver(RegistryKeyTypeIdResolver::class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type")
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        @JsonPropertyOrder(value = ["type"])
        interface Transmuter {

            /**
             * Mutates the given target item using the specified transformation data.
             */
            fun mutate(target: ItemStack, transformation: Transformation, evalResult: RecipeEvaluationResult<*, *>, context: EvaluationContext): ItemStack

        }

    }

}