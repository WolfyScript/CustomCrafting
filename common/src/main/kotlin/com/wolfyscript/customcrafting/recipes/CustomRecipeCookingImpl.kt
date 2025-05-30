package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput

class CustomRecipeCookingImpl(
    override val processing: CustomRecipeCooking.WorkstationProcessing,
    override val result: RecipeResult,
    override val priority: Int,
    override val conditions: RecipeConditions,
) : CustomRecipeCooking {

    override fun evaluate(
        input: RecipeInput.CookingRecipeInput,
        context: EvaluationContext
    ): RecipeData<CustomRecipeCooking>? {
        if (!conditions.areSatisfied(context)) {
            return null
        }
        return processing.evaluate(input, this, context)
    }

    class WorkstationProcessingSmelting(
        override val processingTime: Int, override val source: Ingredient,
    ) : CustomRecipeCooking.WorkstationProcessing.Smelting {

        override fun evaluate(
            input: RecipeInput.CookingRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext
        ): RecipeData<CustomRecipeCooking>? {
            val result = source.match(input.source, true)
            if (result == null) {
                return null
            }
            return RecipeDataImpl(recipe, recipe.result, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

    }

    class WorkstationProcessingBlasting(
        override val processingTime: Int, override val source: Ingredient,
    ) : CustomRecipeCooking.WorkstationProcessing.Blasting {

        override fun evaluate(
            input: RecipeInput.CookingRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext
        ): RecipeData<CustomRecipeCooking>? {
            val result = source.match(input.source, true)
            if (result == null) {
                return null
            }
            return RecipeDataImpl(recipe, recipe.result, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

    }

    class WorkstationProcessingSmoking(
        override val processingTime: Int, override val source: Ingredient,
    ) : CustomRecipeCooking.WorkstationProcessing.Smoking {

        override fun evaluate(
            input: RecipeInput.CookingRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext
        ): RecipeData<CustomRecipeCooking>? {
            val result = source.match(input.source, true)
            if (result == null) {
                return null
            }
            return RecipeDataImpl(recipe, recipe.result, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

    }

    class WorkstationProcessingCampfire(
        override val soulCampfire: Boolean,
        override val normalCampfire: Boolean,
        override val processingTime: Int,
        override val source: Ingredient,
    ) : CustomRecipeCooking.WorkstationProcessing.Campfire {

        override fun evaluate(
            input: RecipeInput.CookingRecipeInput,
            recipe: CustomRecipeCooking,
            context: EvaluationContext
        ): RecipeData<CustomRecipeCooking>? {
            if (context.location == null) {
                return null
            }
            val result = source.match(input.source, true)
            if (result == null) {
                return null
            }
            // TODO: Get proper slot in campfire. Perhaps through the context?
            return RecipeDataImpl(recipe, recipe.result, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

    }

}