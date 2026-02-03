package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import kotlin.random.Random

class RecipeResultImpl(
    override val choices: RecipeChoices,
    override val modifier: RecipeItemModifier,
    override val actions: List<ResultAction> = listOf(),
    override val bulkActions: List<ResultAction> = listOf(),
    override val alwaysKeepPrevious: Boolean,
) : RecipeResult {

    override fun compute(recipeEvaluationResult: RecipeEvaluationResult<*,*>, context: EvaluationContext, random: Random): ScafallItemStack {
        val pickedChoice = choices.all().random(random) // TODO: custom weighting?
        val stack = pickedChoice.create()
        modifier.modify(stack, recipeEvaluationResult, context)
        return stack
    }

    override fun runActions(context: EvaluationContext, count: Int) {
        actions.forEach {
            it.run(context, false)
        }
        if (count > 1) {
            bulkActions.forEach {
                it.run(context, true)
            }
        }
    }

    override fun toString(): String {
        return "{$choices, modified by $modifier, runs $actions and bulk $bulkActions}"
    }

}