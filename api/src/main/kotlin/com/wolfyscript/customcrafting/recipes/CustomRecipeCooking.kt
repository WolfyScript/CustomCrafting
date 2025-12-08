package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.ingredient.Ingredient

interface CustomRecipeCooking : CustomRecipe<RecipeInput.SingleSlotRecipeInput, RecipeEvaluationResult.Data> {

    override val type: RecipeType<CustomRecipeCooking>
        get() = RecipeTypes.cooking.resolveOrThrow()

    val processing: WorkstationProcessing

    val result: RecipeResult

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