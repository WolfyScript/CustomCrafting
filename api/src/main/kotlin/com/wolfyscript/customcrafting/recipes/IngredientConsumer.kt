package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.wolfyscript.scafall.config.jackson.RegistryKeyTypeIdResolver
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

/**
 * Defines how an ingredient is consumed.
 */
@JsonTypeIdResolver(RegistryKeyTypeIdResolver::class)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = ["type"])
interface IngredientConsumer {

    /**
     * Consumes the source item based on the matched reference and count.
     *
     * @return the updated/new [ItemStack] after consumption.
     */
    fun consume(context: EvaluationContext, ref: ItemStackRef, count: Int, source: ItemStack): ItemStack

    /**
     * Consumes the amount from the source item and returns its remains.
     *
     * If the remains can be stored on the source stack or the source stack is empty after consumption, then the remains are returned by [consume].
     * Otherwise, the [consume] has side effects and either stores remains in the inventory or drops them on the ground.
     */
    interface Consume : IngredientConsumer {

        /**
         * The remains this ingredient produces.
         */
        val remains: IngredientRemainder

    }

    /**
     * Replaces the source item with the specified item no matter the amount of the source item.
     */
    interface Replace : IngredientConsumer {

        val replacement: ItemStackRef

    }

    interface Modify : IngredientConsumer {
        // TODO: modify ingredient see ResultModifier
    }

    /**
     * Keeps the source item as is without consuming or modifying it.
     */
    interface Keep : IngredientConsumer

}