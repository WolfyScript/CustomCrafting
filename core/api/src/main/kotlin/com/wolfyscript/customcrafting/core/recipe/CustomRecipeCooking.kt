package com.wolfyscript.customcrafting.core.recipe

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient

/**
 * Represents a custom recipe for cooking operations.
 *
 * This interface defines the contract for recipes that involve cooking items,
 * such as furnace recipes that transform inputs into outputs over time.
 */
interface CustomRecipeCooking : CustomRecipe<RecipeInput.SingleSlotRecipeInput, RecipeEvaluationResult.Data> {

    /**
     * The type of this recipe.
     *
     * This property returns the specific [RecipeType] for cooking recipes.
     */
    override val type: RecipeType<CustomRecipeCooking>
        get() = RecipeTypes.cooking.resolveOrThrow()

    /**
     * The processing configuration for this recipe.
     *
     * Defines how the recipe is processed, including time, cost, and other
     * operational aspects of the cooking process.
     */
    val processing: WorkstationProcessing

    /**
     * The result of this cooking recipe.
     *
     * Specifies the output item(s) produced when the recipe is successfully processed.
     */
    val result: RecipeResult

    /**
     * The experience points (XP) awarded when this recipe is completed.
     *
     * This value determines how much XP the player gains upon successfully
     * completing the cooking recipe.
     */
    val xp: Float

    /**
     * Determines the workstation of the cooking recipe.
     *
     * Each workstation may have its own specific settings.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonPropertyOrder(value = ["type"])
    sealed interface WorkstationProcessing {

        /**
         * The source of the cooking process
         */
        val source: Ingredient

        /**
         * The duration of the cooking process in ticks
         */
        val processingTime: Int

        /**
         * Evaluates the specified recipe for these workstation settings.
         */
        fun evaluate(input: RecipeInput.SingleSlotRecipeInput, recipe: CustomRecipeCooking, context: EvaluationContext): RecipeEvaluationResult.Data?

        @JsonTypeName("blasting")
        interface Blasting : WorkstationProcessing

        @JsonTypeName("smoking")
        interface Smoking : WorkstationProcessing

        @JsonTypeName("smelting")
        interface Smelting : WorkstationProcessing

        @JsonTypeName("campfire")
        interface Campfire : WorkstationProcessing {

            /**
             * Whether the recipe can be processed on a soul campfire.
             */
            val soulCampfire: Boolean

            /**
             * Whether the recipe can be processed on a normal campfire.
             */
            val normalCampfire: Boolean
        }

    }

}