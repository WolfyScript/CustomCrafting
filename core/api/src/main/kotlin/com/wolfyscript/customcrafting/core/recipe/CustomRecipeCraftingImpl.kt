package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.condition.RecipeConditions
import com.wolfyscript.customcrafting.core.recipe.condition.RecipeConditionsImpl
import com.wolfyscript.customcrafting.core.recipe.data.*
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

internal class CustomRecipeCraftingImpl(
    override val priority: Int = 0,
    override val conditions: RecipeConditions = RecipeConditionsImpl(),
    override val formula: CraftingFormula,
    override val result: RecipeResult,
    override val group: String = "",
) : CustomRecipeCrafting {

    override fun evaluate(
        input: RecipeInput.CraftingRecipeInput,
        context: EvaluationContext,
    ): RecipeEvaluationResult.Data? {
        if (!conditions.areSatisfied(context)) {
            return null
        }

        return formula.evaluate(input, this)
    }

    override fun shrink(
        input: RecipeInput.CraftingRecipeInput,
        recipeEvaluationResult: RecipeEvaluationResult<RecipeEvaluationResult.Data, CustomRecipeCrafting>,
        context: EvaluationContext,
        count: Int,
        applyStacks: (Int, ScafallItemStack) -> Unit,
    ) {
        result.runActions(context, count)

        for (value in recipeEvaluationResult.data.nonNullIngredients) {
            var stack = input.matrixData.matrix[value.recipeIndex]
            stack = value.selectedIngredient.shrink(
                stack,
                count,
                value.matchedItemStackRef,
                context,
                recipeEvaluationResult
            )
            applyStacks(value.invSlot, stack)
        }
    }

    override fun toString(): String {
        return "crafting ($priority) with $formula producing $result if $conditions"
    }

}

