package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeDataImpl
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class CustomRecipeCookingImpl(
    override val processingType: CustomRecipeCooking.ProcessingType,
    override val result: RecipeResult,
    override val priority: Int,
    override val conditions: RecipeConditions,
) : CustomRecipeCooking {

    override fun evaluate(
        stack: ItemStack,
        context: EvaluationContext,
    ): RecipeData<CustomRecipeCooking>? {
        if (!conditions.areSatisfied(context)) {
            return null
        }
        return processingType.evaluate(stack, this, context)
    }

    override val type: RecipeType<CustomRecipeCooking>
        get() = TODO("Not yet implemented")

    class ProcessingTypeSmelting(
        override val processingTime: Int, override val source: Ingredient,
    ) : CustomRecipeCooking.ProcessingType.Smelting {

        override fun evaluate(
            stack: ItemStack,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeData<CustomRecipeCooking>? {
            val result = source.match(stack, true)
            if (result == null) {
                return null
            }
            return RecipeDataImpl(recipe, recipe.result, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

    }

    class ProcessingTypeBlasting(
        override val processingTime: Int, override val source: Ingredient,
    ) : CustomRecipeCooking.ProcessingType.Blasting {

        override fun evaluate(
            stack: ItemStack,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeData<CustomRecipeCooking>? {
            val result = source.match(stack, true)
            if (result == null) {
                return null
            }
            return RecipeDataImpl(recipe, recipe.result, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

    }

    class ProcessingTypeSmoking(
        override val processingTime: Int, override val source: Ingredient,
    ) : CustomRecipeCooking.ProcessingType.Smoking {

        override fun evaluate(
            stack: ItemStack,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeData<CustomRecipeCooking>? {
            val result = source.match(stack, true)
            if (result == null) {
                return null
            }
            return RecipeDataImpl(recipe, recipe.result, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

    }

    class ProcessingTypeCampfire(
        override val soulCampfire: Boolean,
        override val normalCampfire: Boolean,
        override val processingTime: Int,
        override val source: Ingredient,
    ) : CustomRecipeCooking.ProcessingType.Campfire {

        override fun evaluate(
            stack: ItemStack,
            recipe: CustomRecipeCooking,
            context: EvaluationContext,
        ): RecipeData<CustomRecipeCooking>? {
            if (context.location == null) {
                return null
            }
            val result = source.match(stack, true)
            if (result == null) {
                return null
            }
            // TODO: Get proper slot in campfire. Perhaps through the context?
            return RecipeDataImpl(recipe, recipe.result, arrayOf(IngredientDataImpl(0, 0, source, result)))
        }

    }

}