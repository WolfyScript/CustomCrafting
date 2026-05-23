package com.wolfyscript.customcrafting.core.recipe.modifier

import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

internal class TransformationImpl(
    override val ingredients: Array<Int>,
    override val transmuter: Transformation.Transmuter
) : Transformation {

    override fun transform(
        target: ScafallItemStack,
        evalResult: RecipeEvaluationResult<*, *>,
        context: EvaluationContext,
    ): ScafallItemStack {
        return transmuter.mutate(target, this, evalResult, context)
    }

    override fun toString(): String {
        return "transformation(${ingredients.contentToString()}, transmuter=$transmuter)"
    }

}