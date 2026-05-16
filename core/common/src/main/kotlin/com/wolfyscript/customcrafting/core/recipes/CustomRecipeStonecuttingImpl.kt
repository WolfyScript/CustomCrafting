package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.core.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.core.recipes.data.DefaultDataImpl
import com.wolfyscript.customcrafting.core.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient

class CustomRecipeStonecuttingImpl(
    override val priority: Int = 0,
    override val conditions: RecipeConditions = RecipeConditionsImpl(),
    override val source: Ingredient,
    override val result: RecipeResult,
    override val flattenResult: Boolean,
    override val group: String = ""
) : CustomRecipeStonecutting {

    override fun evaluate(
        input: RecipeInput.SingleSlotRecipeInput,
        context: EvaluationContext
    ): RecipeEvaluationResult.Data? {
        if (!conditions.areSatisfied(context)) {
            return null
        }

        val matchResult = source.match(input.source) ?: return null

        return DefaultDataImpl(arrayOf(IngredientDataImpl(0, 0, source, matchResult)))
    }


}