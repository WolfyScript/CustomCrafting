package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

class RecipeItemModifierImpl(override val transformations: List<RecipeItemModifier.Transformation> = emptyList()) : RecipeItemModifier {

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

    class ResultModifierTransformationImpl(
        override val ingredients: Array<Int>,
        override val transmuter: RecipeItemModifier.Transformation.Transmuter
    ) : RecipeItemModifier.Transformation {

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

}
