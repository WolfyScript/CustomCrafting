package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.RemainsIgnoreOptions
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.minecraft.wrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

internal class IngredientRemainderCustomImpl(
    override val ignore: RemainsIgnoreOptions = RemainsIgnoreOptionsImpl(vanilla = false, others = false),
    override val remainder: ItemStackRef,
) : IngredientRemainder.Custom {

    override fun calculate(
        target: ItemStackSnapshot,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>,
    ): List<ScafallItemStack> {
        val mcSource = target.unwrap()
        val customRemainder = remainder.create()
        val vanillaRemainder = mcSource.item.craftingRemainder

        if (!ignore.vanilla && vanillaRemainder != null) {
            return listOf(vanillaRemainder.create().wrap())
        }
        if (!ignore.others) {
            // TODO: determine remains from third-party mods/plugins
        }

        return listOf(customRemainder)
    }

    override fun toString(): String {
        return "(ignore=$ignore, remainder=$remainder)"
    }

}