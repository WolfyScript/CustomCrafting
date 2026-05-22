package com.wolfyscript.customcrafting.core.recipe.modifier

import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

internal class RecipeItemModifierImpl(override val transformations: List<Transformation> = emptyList()) : RecipeItemModifier {

    override fun modify(
        target: ScafallItemStack,
        evalResult: RecipeEvaluationResult<*, *>,
        context: EvaluationContext,
    ): ScafallItemStack {
        transformations.forEach { it.transform(target, evalResult, context) }
        return target
    }

    override fun toString(): String {
        return "$transformations"
    }

}
