package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeInput

class CustomRecipeStonecuttingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val source: Ingredient,
    override val result: RecipeResult
) : CustomRecipeStonecutting {

    override fun evaluate(
        input: RecipeInput.StonecuttingRecipeInput,
        context: EvaluationContext
    ): RecipeData<CustomRecipeStonecutting>? {
        if (!conditions.areSatisfied(context)) {
            return null
        }

        val matchResult = source.match(input.source)
        if (matchResult == null) {
            return null
        }

        return RecipeDataImpl(this, arrayOf(IngredientDataImpl(0, 0, source, matchResult)))
    }


}