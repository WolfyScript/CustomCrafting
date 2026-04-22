package com.wolfyscript.customcrafting.core.recipes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.core.recipes.ingredient.Ingredient
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientConsumer
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.core.recipes.ingredient.IngredientRemainder
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.minecraft.unwrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStackLike
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.wolfyscript.scafall.wrappers.minecraft.wrap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.util.Mth
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Items
import java.io.IOException
import kotlin.random.Random

class IngredientImpl(
    override val choices: RecipeChoices,
    override val matching: IngredientMatcher = IngredientMatcherExactImpl(),
    override val consumption: IngredientConsumer = IngredientConsumerConsumeImpl(IngredientRemainderDefaultImpl()),
) : Ingredient {

    override fun match(stack: ScafallItemStack): ItemStackRef? {
        if (stack.amount <= 0 || stack.unwrap().let { it.isEmpty || it.item == Items.AIR }) {
            return null
        }
        return matching.match(this, stack)
    }

    override fun shrink(
        target: ScafallItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>,
    ): ScafallItemStack {
        return consumption.consume(target, count, ref, context, evalResult)
    }

    override fun toString(): String {
        return "{$choices, $matching, $consumption}"
    }

}

// TODO: incase the other system of looking up the key doesn't work, lets use this
private class IngredientRef(val key: Key) : Ingredient {

    val ingredient: Ingredient by lazy {
        CustomCraftingProvider.get().server?.ingredientManager?.getIngredient(key)
            ?: error("Could not find required ingredient: $key")
    }

    override val choices: RecipeChoices
        get() = ingredient.choices
    override val matching: IngredientMatcher
        get() = ingredient.matching
    override val consumption: IngredientConsumer
        get() = ingredient.consumption

    override fun match(stack: ScafallItemStack): ItemStackRef? {
        return ingredient.match(stack)
    }

    override fun shrink(
        target: ScafallItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>,
    ): ScafallItemStack {
        return ingredient.shrink(target, count, ref, context, evalResult)
    }

}

class IngredientMatcherExactImpl : IngredientMatcher.Exact {

    override fun match(
        ingredient: Ingredient,
        source: ItemStackLike,
    ): ItemStackRef? {
        return ingredient.choices.all().firstOrNull { choice ->
            choice.matches(source, true)
        }
    }

    override fun toString(): String {
        return "exact"
    }

}

class IngredientMatcherItemImpl(
    override val mustContain: Set<Key> = emptySet(),
    override val mustNotContain: Set<Key> = emptySet(),
) : IngredientMatcher.Item {

    override fun match(
        ingredient: Ingredient,
        source: ItemStackLike,
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

    override fun toString(): String {
        return "(mustContain=$mustContain, mustNotContain=$mustNotContain)"
    }

}

class IngredientConsumerConsumeImpl(override val remains: IngredientRemainder) : IngredientConsumer.Consume {

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

class IngredientConsumerReplaceImpl(override val replacement: ItemStackRef) : IngredientConsumer.Replace {

    override fun consume(
        target: ScafallItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>,
    ): ScafallItemStack {
        return replacement.create()
    }

    override fun toString(): String {
        return "($replacement)"
    }

}

class IngredientConsumerKeepImpl(override val modifier: RecipeItemModifier = RecipeItemModifierImpl()) :
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

data class RemainsIgnoreOptionsImpl(override val vanilla: Boolean, override val others: Boolean) :
    RemainsIgnoreOptions {

    override fun toString(): String {
        return "(vanilla=$vanilla, others=$others)"
    }
}

class IngredientRemainderCustomImpl(
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

        if (!ignore.vanilla && !mcSource.item.craftingRemainder.isEmpty) {
            return listOf(mcSource.item.craftingRemainder.wrap())
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

class IngredientRemainderDefaultImpl(
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

        if (!ignore.vanilla) {
            val mcStack = target.unwrap()
            remains.add(mcStack.item.craftingRemainder.wrap())
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

class IngredientSerializerModifier : BeanSerializerModifier() {
    override fun modifySerializer(
        config: SerializationConfig,
        beanDesc: BeanDescription,
        serializer: JsonSerializer<*>,
    ): JsonSerializer<*> {
        if (beanDesc.beanClass.isAssignableFrom(Ingredient::class.java)) {
            return Serializer(serializer as JsonSerializer<Ingredient>)
        }
        return serializer
    }

    private class Serializer(
        private val defaultSerializer: JsonSerializer<Ingredient>,
    ) :
        StdSerializer<Ingredient>(defaultSerializer.handledType()) {

        @Throws(IOException::class)
        override fun serialize(targetObject: Ingredient, generator: JsonGenerator, provider: SerializerProvider) {
            try {
                val key = CustomCraftingProvider.get().server?.ingredientManager?.getKey(targetObject)
                if (key != null) {
                    generator.writeObject(key)
                    return
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            defaultSerializer.serialize(targetObject, generator, provider)
        }
    }
}

class IngredientDeserializerModifier : BeanDeserializerModifier() {
    override fun modifyDeserializer(
        config: DeserializationConfig,
        beanDesc: BeanDescription,
        deserializer: JsonDeserializer<*>,
    ): JsonDeserializer<*> {
        if (beanDesc.beanClass == Ingredient::class.java) {
            return Deserializer(deserializer as JsonDeserializer<Ingredient>)
        }
        return deserializer
    }

    private class Deserializer(
        private val defaultDeserializer: JsonDeserializer<Ingredient>,
    ) :
        StdDeserializer<Ingredient>(defaultDeserializer.handledType()), ResolvableDeserializer {

        @Throws(IOException::class)
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Ingredient? {
            if (p.isExpectedStartObjectToken) {
                return defaultDeserializer.deserialize(p, ctxt)
            }
            return getKeyedObject(p)
        }

        @Throws(JsonMappingException::class)
        override fun resolve(ctxt: DeserializationContext) {
            if (defaultDeserializer is ResolvableDeserializer) {
                defaultDeserializer.resolve(ctxt)
            }
        }

        @Throws(IOException::class)
        override fun deserializeWithType(
            p: JsonParser,
            ctxt: DeserializationContext,
            typeDeserializer: TypeDeserializer,
        ): Any? {
            if (p.isExpectedStartObjectToken) {
                return defaultDeserializer.deserializeWithType(p, ctxt, typeDeserializer)
            }
            return getKeyedObject(p)
        }

        @Throws(IOException::class)
        fun getKeyedObject(p: JsonParser): Ingredient? {
            val value = p.readValueAs(String::class.java)
            val key = Key.parse(value)
            return CustomCraftingProvider.get().server?.ingredientManager?.getIngredient(key)
        }
    }
}