package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.wolfyscript.scafall.config.jackson.RegistryKeyTypeIdResolver
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ItemStackLike

/**
 * Checks if a source item stack matches the given criteria.
 */
@JsonTypeIdResolver(RegistryKeyTypeIdResolver::class)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = ["type"])
interface IngredientMatcher {

    fun match(ingredient: Ingredient, source: ItemStackLike<*,*>): ItemStackRef?

    /**
     * Checks if the item type and the components match.
     * Additionally, checks if the item contains the specified components [mustContain].
     */
    interface Exact : IngredientMatcher

    /**
     * Only checks if the item type matches.
     */
    interface Item : IngredientMatcher {

        /**
         * The components the stack must contain to pass the check.
         *
         * Default: No components (empty list).
         */
        val mustContain: Set<Key>

        /**
         * The components the stack must **not** contain to pass the check.
         *
         * Default: No components (empty list).
         */
        val mustNotContain: Set<Key>

    }

}