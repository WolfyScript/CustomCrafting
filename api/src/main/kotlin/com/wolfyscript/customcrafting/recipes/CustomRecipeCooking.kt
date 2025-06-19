package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput

interface CustomRecipeCooking : CustomRecipe<RecipeInput.CookingRecipeInput, CustomRecipeCooking> {

    override val type: RecipeType<CustomRecipeCooking>
        get() = RecipeTypes.cooking.resolveOrThrow()

    val processing: WorkstationProcessing

    val result: RecipeResult

    val xp: Float

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonPropertyOrder(value = ["type"])
    sealed interface WorkstationProcessing {

        val source: Ingredient

        val processingTime: Int

        fun evaluate(input: RecipeInput.CookingRecipeInput, recipe: CustomRecipeCooking, context: EvaluationContext): RecipeData<CustomRecipeCooking>?

        @JsonTypeName("blasting")
        interface Blasting : WorkstationProcessing

        @JsonTypeName("smoking")
        interface Smoking : WorkstationProcessing

        @JsonTypeName("smelting")
        interface Smelting : WorkstationProcessing

        @JsonTypeName("campfire")
        interface Campfire : WorkstationProcessing {

            /**
             * Weather the recipe can be processed on a soul campfire.
             */
            val soulCampfire: Boolean

            /**
             * Weather the recipe can be processed on a normal campfire.
             */
            val normalCampfire: Boolean
        }

    }

}