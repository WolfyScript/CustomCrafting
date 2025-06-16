package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.wolfyscript.scafall.config.jackson.RegistryKeyTypeIdResolver

@JsonTypeIdResolver(RegistryKeyTypeIdResolver::class)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = ["type"])
interface ResultAction {

    fun run(context: EvaluationContext, bulk: Boolean = false)

}