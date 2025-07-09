package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.data.RepairingRecipeDataImpl
import com.wolfyscript.customcrafting.recipes.process.ProcessRepairing

class CustomRecipeRepairingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val process: ProcessRepairing,
    override val base: Ingredient,
    override val addition: Ingredient?,
) : CustomRecipeRepairing {

    override fun evaluate(
        input: RecipeInput.RepairingRecipeInput,
        context: EvaluationContext,
    ): RecipeEvaluationResult.RepairingRecipeData? {
        val matchedBase = base.match(input.base)?.let { baseMatch ->
            IngredientDataImpl(0, 0, base, baseMatch)
        } ?: return null

        if (addition == null && input.addition != null || addition != null && input.addition == null) {
            return null
        }
        val matchedAddition = addition?.match(input.addition!!)?.let { additionMatch ->
            IngredientDataImpl(1, 1, addition, additionMatch)
        } ?: return null

        return RepairingRecipeDataImpl(0, arrayOf(matchedBase, matchedAddition))
    }

    override fun toString(): String {
        return "Repairing ($priority) $base with $addition if $conditions producing $process"
    }

}

