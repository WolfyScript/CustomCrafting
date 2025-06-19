package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import kotlin.random.Random

class RecipeResultImpl(
    override val choices: RecipeChoices,
    override val modifier: ResultModifier,
    override val actions: List<ResultAction> = listOf(),
    override val bulkActions: List<ResultAction> = listOf(),
) : RecipeResult {

    override fun compute(recipeData: RecipeData<*>, context: EvaluationContext, random: Random): ItemStack {
        val pickedChoice = choices.allFor(context).random(random) // TODO: custom weighting?
        val stack = pickedChoice.create()
        modifier.modify(recipeData, stack, context)
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