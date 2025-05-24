package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver
import com.wolfyscript.scafall.config.jackson.KeyedTypeIdResolver
import com.wolfyscript.scafall.config.jackson.KeyedTypeResolver

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonTypeResolver(KeyedTypeResolver::class)
@JsonTypeIdResolver(KeyedTypeIdResolver::class)
@JsonPropertyOrder("type")
interface CustomRecipe {

    val type: RecipeType<*>

    val priority: Int

    val conditions: RecipeConditions

}