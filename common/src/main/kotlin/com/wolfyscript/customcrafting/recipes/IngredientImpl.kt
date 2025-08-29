package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.utils.unwrap
import com.wolfyscript.scafall.wrappers.utils.wrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.scafall.wrappers.world.items.ItemStackLike
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.util.Mth
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Items
import kotlin.random.Random

class IngredientImpl(
    override val choices: RecipeChoices,
    override val matching: IngredientMatcher = IngredientMatcherExactImpl(),
    override val consumption: IngredientConsumer = IngredientConsumerConsumeImpl(IngredientRemainderDefaultImpl()),
) : Ingredient {

    override fun match(stack: ItemStack): ItemStackRef? {
        if (stack.amount <= 0 || stack.unwrap().let { it.isEmpty || it.item == Items.AIR }) {
            return null
        }
        return matching.match(this, stack)
    }

    override fun shrink(
        target: ItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>,
    ): ItemStack {
        return consumption.consume(target, count, ref, context, evalResult)
    }

    override fun toString(): String {
        return "{$choices, $matching, $consumption}"
    }

}

class IngredientMatcherExactImpl() : IngredientMatcher.Exact {

    override fun match(
        ingredient: Ingredient,
        source: ItemStackLike<*, *>,
    ): ItemStackRef? {
        return ingredient.choices.all().firstOrNull { choice ->
            choice.matches(source, true)
        }
    }

}

class IngredientMatcherItemImpl(
    override val mustContain: Set<Key> = emptySet(),
    override val mustNotContain: Set<Key> = emptySet(),
) : IngredientMatcher.Item {

    override fun match(
        ingredient: Ingredient,
        source: ItemStackLike<*, *>,
    ): ItemStackRef? {
        return ingredient.choices.all().firstOrNull { choice ->
            if (!choice.matches(source, false)) {
                return@firstOrNull false
            }
            val mcStack = source.unwrap()
            if (mustContain.isNotEmpty() && mustContain.any { key ->
                    !BuiltInRegistries.DATA_COMPONENT_TYPE.get(key.toMc())
                        .map { mcStack.hasNonDefault(it.value()) }
                        .orElse(true)
                }) {
                return@firstOrNull false
            }
            if (mustNotContain.isNotEmpty() && mustNotContain.any { key ->
                    BuiltInRegistries.DATA_COMPONENT_TYPE.get(key.toMc())
                        .map { mcStack.hasNonDefault(it.value()) }
                        .orElse(false)
                }) {
                return@firstOrNull false
            }
            return@firstOrNull true
        }
    }

}

class IngredientConsumerConsumeImpl(override val remains: IngredientRemainder) : IngredientConsumer.Consume {

    override fun consume(
        target: ItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>,
    ): ItemStack {
        val mcStack = target.unwrap()
        mcStack.shrink(count * ref.amount)

        val remainingItems = remains.calculate(target, count, ref, context, evalResult)
        if (remainingItems.isEmpty()) {
            return mcStack.wrap()
        }
        val finalStack = if (mcStack.isEmpty) {
            remainingItems.firstOrNull() ?: net.minecraft.world.item.ItemStack.EMPTY.wrap()
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
                    val f: Float = Random.nextFloat() * 0.5f
                    val g: Float = Random.nextFloat() * (Math.PI.toFloat() * 2f)
                    itemEntity.setDeltaMovement((-Mth.sin(g) * f).toDouble(), 0.2, (Mth.cos(g) * f).toDouble())
                    level.addFreshEntity(itemEntity)
                }
            }
        }
        return finalStack
    }

}

class IngredientConsumerReplaceImpl(override val replacement: ItemStackRef) : IngredientConsumer.Replace {

    override fun consume(
        target: ItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>
    ): ItemStack {
        return replacement.create()
    }

}

class IngredientConsumerKeepImpl(override val modifier: RecipeItemModifier = RecipeItemModifierImpl()) : IngredientConsumer.Keep {

    override fun consume(
        target: ItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>
    ): ItemStack {
        return target
    }

}

data class RemainsIgnoreOptionsImpl(override val vanilla: Boolean, override val others: Boolean) : RemainsIgnoreOptions

class IngredientRemainderCustomImpl(
    override val ignore: RemainsIgnoreOptions = RemainsIgnoreOptionsImpl(vanilla = false, others = false),
    override val remainder: ItemStackRef,
) : IngredientRemainder.Custom {

    override fun calculate(
        target: ItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>
    ): List<ItemStack> {
        val mcSource = target.unwrap()
        val customRemainder = remainder.create()

        if (!ignore.vanilla && !mcSource.item.craftingRemainder.isEmpty) {
            return listOf(mcSource.item.craftingRemainder.wrap())
        }
        if (!ignore.others) {
            // TODO: determine remains from third-party mods/plugins
        }

        return listOf(customRemainder)
    }

}

class IngredientRemainderDefaultImpl(
    override val ignore: RemainsIgnoreOptions = RemainsIgnoreOptionsImpl(vanilla = false, others = false),
) : IngredientRemainder.Default {

    override fun calculate(
        target: ItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>
    ): List<ItemStack> {

        val remains = mutableListOf<ItemStack>()

        if (!ignore.vanilla) {
            val mcStack = target.unwrap()
            remains.add(mcStack.item.craftingRemainder.wrap())
        }

        if (!ignore.others) {
            // TODO: determine remains from third-party mods/plugins
        }

        return remains
    }

}