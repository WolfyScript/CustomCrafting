package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.scafall.config.jackson.RegistryKeyTypeIdResolver
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

/**
 * Defines how an ingredient is consumed.
 *
 * This component is part of a [Registry][com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes.ingredientConsumers], so third-parties can add their own custom consumers.
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
     * @param target the target item stack to consume.
     * @param count how many instances of the ingredients are to be consumed (Spigot/Paper CC may bulk consume the recipe. On Fabric recipes are consumed one-by-one)
     * @param ref the reference associated with the target.
     * @param context the evaluation context (e.g. player, tile entity, etc.).
     * @param evalResult the result of the recipe evaluation (ingredient info like which ingredient is present in each slot).
     *
     * @return the updated/new [ScafallItemStack] after consumption.
     */
    fun consume(target: ScafallItemStack, count: Int, ref: ItemStackRef, context: EvaluationContext, evalResult: RecipeEvaluationResult<*, *>): ScafallItemStack

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

    /**
     * Keeps the source item as is without consuming it.
     */
    interface Keep : IngredientConsumer {

        /**
         * Modify the source item.
         * Optional; if not declared, no modification is done.
         */
        val modifier: RecipeItemModifier

    }

}