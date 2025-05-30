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
        if (value is CustomRecipe<*,*>) {
            return CustomCraftingProvider.get().registries.recipeTypes.getKey(value.type)?.toString()
                ?: throw IllegalArgumentException("No key found for recipe $value!")
        }
        throw IllegalArgumentException("Value must be a CustomRecipe!")
    }

    override fun typeFromId(context: DatabindContext?, id: String?): JavaType? {
        if (id == null) {
            return TypeFactory.unknownType()
        }
        val recipeType = CustomCraftingProvider.get().registries.recipeTypes[Key.parse(id)]
        if (recipeType != null) {
            return context?.constructSpecializedType(superType, recipeType.recipeClass)
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