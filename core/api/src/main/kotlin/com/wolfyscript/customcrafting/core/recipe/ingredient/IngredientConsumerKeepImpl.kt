package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.modifier.RecipeItemModifier
import com.wolfyscript.customcrafting.core.recipe.modifier.RecipeItemModifierImpl
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

internal class IngredientConsumerKeepImpl(override val modifier: RecipeItemModifier = RecipeItemModifierImpl()) :
    IngredientConsumer.Keep {

    override fun consume(
        target: ScafallItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>,
    ): ScafallItemStack {
        return target
    }

    override fun toString(): String {
        return "(modifier=$modifier)"
    }

}