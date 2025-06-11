package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase
import com.fasterxml.jackson.databind.type.TypeFactory
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.identifier.Key

class RecipeTypeIdResolver : TypeIdResolverBase() {

    private lateinit var superType: JavaType

    override fun init(bt: JavaType?) {
        super.init(bt)
        superType = bt ?: throw IllegalArgumentException("Failed to initialize recipe type resolver: missing super type!")
    }

    override fun idFromValue(value: Any?): String? {
        return getKey(value)
    }

    override fun idFromValueAndType(value: Any?, suggestedType: Class<*>?): String? {
        return getKey(value)
    }

    private fun getKey(value: Any?): String {
        if (value == null) {
            throw IllegalArgumentException("Failed to get recipe type null type!")
        }
        if (value is CustomRecipe<*,*>) {
            val key = CustomCraftingProvider.get().registries.recipeTypes.getKey(value.type)
            if (key != null) {
                return key.toString()
            }
            throw IllegalArgumentException("Failed to get recipe type key of recipe class: ${value::class.java}! Make sure the type it uses is registered.")
        }
        throw IllegalArgumentException("Failed to get recipe type of class: ${value::class.java}! That is not a recipe.")
    }

    override fun typeFromId(context: DatabindContext, id: String): JavaType {
        val namespacedKey = if (id.contains(':')) {
            Key.parse(id)
        } else {
            Key.key(Key.CUSTOMCRAFTING_NAMESPACE, id)
        }
        val value = CustomCraftingProvider.get().registries.recipeTypes[namespacedKey]
        if (value != null) {
            return context.constructSpecializedType(superType, value.recipeClass)
        }
        return TypeFactory.unknownType()
    }

    override fun getMechanism(): JsonTypeInfo.Id? {
        return JsonTypeInfo.Id.CUSTOM
    }

}

class RecipeTypeResolver : StdTypeResolverBuilder() {

    override fun buildTypeSerializer(
        config: SerializationConfig,
        baseType: JavaType,
        subtypes: Collection<NamedType>
    ): TypeSerializer? {
        return if (useForType(baseType)) super.buildTypeSerializer(config, baseType, subtypes) else null
    }

    override fun buildTypeDeserializer(
        config: DeserializationConfig,
        baseType: JavaType,
        subtypes: Collection<NamedType>
    ): TypeDeserializer? {
        return if (useForType(baseType)) super.buildTypeDeserializer(config, baseType, subtypes) else null
    }

    fun useForType(t: JavaType): Boolean {
        return t.isTypeOrSubTypeOf(CustomRecipe::class.java)
    }

}