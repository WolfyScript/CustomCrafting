package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class RecipeResultImpl(
    override val choices: RecipeChoices,
    override val modifier: ResultModifier,
    override val actions: List<ResultAction>,
    override val bulkActions: List<ResultAction>
) : RecipeResult {

    override fun computeOrGet(recipeData: RecipeData<*>, context: EvaluationContext): ItemStack {
        val pickedChoice = choices.allFor(context).random() // TODO: custom weighting
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

}