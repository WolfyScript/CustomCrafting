package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

class ResultModifierImpl(override val transformations: List<ResultModifier.Transformation>) : ResultModifier {

    override fun modify(
        recipeData: RecipeData<*>,
        result: ItemStack,
        context: EvaluationContext,
    ): ItemStack {
        transformations.forEach { it.transform(recipeData, result, context) }
        return result
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

    }

}
