package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.RemainsIgnoreOptions
import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.minecraft.wrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

internal class IngredientRemainderDefaultImpl(
    override val ignore: RemainsIgnoreOptions = RemainsIgnoreOptionsImpl(vanilla = false, others = false),
) : IngredientRemainder.Default {

    override fun calculate(
        target: ItemStackSnapshot,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>,
    ): List<ScafallItemStack> {

        val remains = mutableListOf<ScafallItemStack>()
        val mcStack = target.unwrap()
        val vanillaRemainder = mcStack.item.craftingRemainder

        if (!ignore.vanilla && vanillaRemainder != null) {
            remains.add(vanillaRemainder.create().wrap())
        }

        if (!ignore.others) {
            // TODO: determine remains from third-party mods/plugins
        }

        return remains
    }

    override fun toString(): String {
        return "(ignore=$ignore)"
    }

}