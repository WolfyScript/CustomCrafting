package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.minecraft.unwrap
import com.wolfyscript.scafall.wrappers.minecraft.wrap
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import net.minecraft.util.Mth
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import kotlin.random.Random

internal class IngredientConsumerConsumeImpl(override val remains: IngredientRemainder) : IngredientConsumer.Consume {

    override fun consume(
        target: ScafallItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>,
    ): ScafallItemStack {
        val remainingItems = remains.calculate(target.snapshot(), count, ref, context, evalResult)
        val mcStack = target.unwrap()
        mcStack.shrink(count * ref.amount)

        if (remainingItems.isEmpty()) {
            return mcStack.wrap()
        }
        val finalStack = if (mcStack.isEmpty) {
            remainingItems.firstOrNull() ?: ItemStack.EMPTY.wrap()
        } else {
            mcStack.wrap()
        }

        val startIndex = if (mcStack.isEmpty) 1 else 0
        if (remainingItems.size > startIndex) {
            var items = remainingItems.drop(startIndex)
            val player = context.player?.unwrap()
            if (player != null) {
                items = items.filterNot {
                    player.inventory.add(it.unwrap())
                }
                items.forEach {
                    player.drop(it.unwrap(), true, false)
                }
                return finalStack
            }

            val blockEntity = context.blockEntity?.unwrap()
            if (blockEntity != null) {
                items.forEach {
                    val level = blockEntity.level ?: return@forEach
                    val pos = blockEntity.blockPos.center
                    val itemEntity = ItemEntity(level, pos.x, pos.y, pos.z, it.unwrap())
                    itemEntity.setPickUpDelay(40)
                    val f: Double = Random.nextFloat() * 0.5
                    val g: Double = Random.nextFloat() * Math.PI * 2.0
                    itemEntity.setDeltaMovement((-Mth.sin(g) * f), 0.2, Mth.cos(g) * f)
                    level.addFreshEntity(itemEntity)
                }
            }
        }
        return finalStack
    }

    override fun toString(): String {
        return "(remains=$remains)"
    }

}