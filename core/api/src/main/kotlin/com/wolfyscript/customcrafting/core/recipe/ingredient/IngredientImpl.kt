package com.wolfyscript.customcrafting.core.recipe.ingredient

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
import com.wolfyscript.customcrafting.core.recipe.CraftingFormula
import com.wolfyscript.customcrafting.core.recipe.CraftingFormulaShapedImpl
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.RecipeChoices
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.ShapedRecipePattern
import java.io.IOException

internal class IngredientImpl(
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

fun CraftingFormula.Shaped.toShapedRecipePattern(): ShapedRecipePattern {
    this as CraftingFormulaShapedImpl
    val ingredients = this.mappedIngredients.mapValues { net.minecraft.world.item.crafting.Ingredient.of(*it.value.choices.stacks.map { stack -> stack.create().unwrap().item }.toTypedArray()) }
    return ShapedRecipePattern.of(ingredients, shape.rows)
}
