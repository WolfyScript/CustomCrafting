package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions
import com.wolfyscript.customcrafting.recipes.data.GrindingRecipeDataImpl
import com.wolfyscript.customcrafting.recipes.data.IngredientDataImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.customcrafting.recipes.grinding.GrindingProcess

class CustomRecipeGrindingImpl(
    override val priority: Int,
    override val conditions: RecipeConditions,
    override val base: Ingredient,
    override val addition: Ingredient?,
    override val process: GrindingProcess,
) : CustomRecipeGrinding {

    override fun evaluate(
        input: RecipeInput.GrindingRecipeInput,
        context: EvaluationContext,
    ): RecipeEvaluationResult.GrindingRecipeData? {

        var baseStack = input.base
        var additionStack = input.addition

        if (baseStack == null && additionStack == null) {
            return null
        }

        var baseInvSlot = 0
        if (addition == null) {
            // This recipe has no addition, so the stack may be placed into the base or addition slot
            if (baseStack == null) {
                baseInvSlot = 1
                baseStack = input.addition // So lets take the addition and use it as if it were placed in the base slot
                additionStack = null
            }
        }

        if (baseStack == null) {
            return null // the base slot should never be emtpy at this point
        }

        val matchedBase = base.match(baseStack)?.let { baseMatch ->
            IngredientDataImpl(baseInvSlot, 0, base, baseMatch)
        } ?: return null

        if (addition == null && additionStack != null || addition != null && additionStack == null) {
            return null
        }
        val matchedAddition = addition?.match(additionStack!!)?.let { additionMatch ->
            IngredientDataImpl(1, 1, addition, additionMatch)
        }

        return GrindingRecipeDataImpl(
            if (baseInvSlot == 0) arrayOf(matchedBase, matchedAddition) else arrayOf(null, matchedBase)
        )
    }

}