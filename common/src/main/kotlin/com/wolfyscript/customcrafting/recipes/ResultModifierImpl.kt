package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class ResultModifierImpl(override val transformations: List<ResultModifier.Transformation> = listOf()) : ResultModifier {

    override fun modify(
        recipeEvaluationResult: RecipeEvaluationResult<*, *>,
        result: ItemStack,
        context: EvaluationContext,
    ): ItemStack {
        transformations.forEach { it.transform(recipeEvaluationResult, result, context) }
        return result
    }

    override fun toString(): String {
        return "$transformations"
    }

    class ResultModifierTransformationImpl(
        override val ingredients: Array<Int>,
        override val transmuter: ResultModifier.Transformation.Transmuter
    ) : ResultModifier.Transformation {

        override fun transform(
            recipeEvaluationResult: RecipeEvaluationResult<*, *>,
            result: ItemStack,
            context: EvaluationContext,
        ): ItemStack {
            return transmuter.mutate(this, recipeEvaluationResult, result, context)
        }

        override fun toString(): String {
            return "transformation(${ingredients.contentToString()}, transmuter=$transmuter)"
        }

    }

}
