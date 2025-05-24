package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeDataImpl
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class CustomRecipeStonecuttingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val source: Ingredient,
    override val result: RecipeResult
) : CustomRecipeStonecutting {

    override fun evaluate(
        context: EvaluationContext,
        stack: ItemStack,
    ): RecipeData<CustomRecipeStonecutting>? {
        if (!conditions.areSatisfied(context)) {
            return null
        }

        val matchResult = source.match(stack, true)
        if (matchResult == null) {
            return null
        }

        return RecipeDataImpl(this, result, arrayOf(IngredientDataImpl(0, 0, source, matchResult)))
    }


}