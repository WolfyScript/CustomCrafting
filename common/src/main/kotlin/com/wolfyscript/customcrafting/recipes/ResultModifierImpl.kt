package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class ResultModifierImpl(override val transformations: List<ResultModifier.Transformation> = listOf()) : ResultModifier {

    override fun modify(
        recipeData: RecipeData<*>,
        result: ItemStack,
        context: EvaluationContext,
    ): ItemStack {
        transformations.forEach { it.transform(recipeData, result, context) }
        return result
    }

    override fun toString(): String {
        return "ResultModifierImpl(transformations=$transformations)"
    }

    class ResultModifierTransformationImpl(
        override val ingredients: Array<Int>,
        override val transmuter: ResultModifier.Transformation.Transmuter
    ) : ResultModifier.Transformation {

        override fun transform(
            recipeData: RecipeData<*>,
            result: ItemStack,
            context: EvaluationContext,
        ): ItemStack {
            return transmuter.mutate(this, recipeData, result, context)
        }

        override fun toString(): String {
            return "ResultModifierTransformationImpl(ingredients=${ingredients.contentToString()}, transmuter=$transmuter)"
        }

    }

}
