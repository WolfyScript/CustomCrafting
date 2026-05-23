package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.core.recipe.condition.RecipeConditions
import com.wolfyscript.customcrafting.core.recipe.condition.RecipeConditionsImpl
import com.wolfyscript.customcrafting.core.recipe.evaluation.GrindingRecipeDataImpl
import com.wolfyscript.customcrafting.core.recipe.evaluation.IngredientDataImpl
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeInput
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.ingredient.Ingredient
import com.wolfyscript.customcrafting.core.recipe.process.ProcessGrinding

internal class CustomRecipeGrindingImpl(
    override val priority: Int = 0,
    override val conditions: RecipeConditions = RecipeConditionsImpl(),
    override val base: Ingredient,
    override val addition: Ingredient? = null,
    override val process: ProcessGrinding,
    override val group: String = "",
) : CustomRecipeGrinding {

    override fun evaluate(
        input: RecipeInput.GrindingRecipeInput,
        context: EvaluationContext,
    ): RecipeEvaluationResult.GrindingRecipeData? {

        var baseStack = input.base
        var additionStack = input.addition

        val emptyBase = baseStack == null || baseStack.isEmpty
        val emptyAddition = additionStack == null || additionStack.isEmpty

        if (emptyBase && emptyAddition) {
            return null
        }

        var baseInvSlot = 0
        if (addition == null) {
            // This recipe has no addition, so the stack may be placed into the base or addition slot
            if (emptyBase) {
                baseInvSlot = 1
                baseStack = input.addition // So lets take the addition and use it as if it were placed in the base slot
                additionStack = null
            }
        }

        if (baseStack == null || baseStack.isEmpty) {
            return null // the base slot should never be emtpy at this point
        }

        val matchedBase = base.match(baseStack)?.let { baseMatch ->
            IngredientDataImpl(baseInvSlot, 0, base, baseMatch)
        } ?: return null

        if (addition == null && !emptyAddition || addition != null && emptyAddition) {
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