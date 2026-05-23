package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

internal class IngredientConsumerReplaceImpl(override val replacement: ItemStackRef) : IngredientConsumer.Replace {

    override fun consume(
        target: ScafallItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>,
    ): ScafallItemStack {
        return replacement.create()
    }

    override fun toString(): String {
        return "($replacement)"
    }

}