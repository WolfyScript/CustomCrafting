package com.wolfyscript.customcrafting.core.recipes.ingredient

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.recipes.EvaluationContext
import com.wolfyscript.customcrafting.core.recipes.RemainsIgnoreOptions
import com.wolfyscript.customcrafting.core.recipes.data.RecipeEvaluationResult
import com.wolfyscript.scafall.config.jackson.RegistryKeyTypeIdResolver
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot

/**
 * Defines how the remainders of an ingredient are calculated.
 *
 * This is part of a [Registry][com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes.ingredientRemainders], so third-parties can add their own custom remainder calculators.
 */
@JsonTypeIdResolver(RegistryKeyTypeIdResolver::class)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = ["type"])
interface IngredientRemainder {

    /**
     * Calculates the remainders of the given ingredient based on the given count and context.
     *
     * @param target the target item stack to calculate the remainder for (pre-consumption).
     * @param count how many instances of the ingredients are to be consumed (Spigot/Paper CC may bulk consume the recipe. On Fabric recipes are consumed one-by-one)
     * @param ref the reference associated with the target (pre-consumption).
     * @param context the evaluation context (e.g. player, tile entity, etc.).
     * @param evalResult the result of the recipe evaluation (ingredient info like which ingredient is present in each slot).
     *
     * @return the list of remaining item stacks.
     */
    fun calculate(target: ItemStackSnapshot, count: Int, ref: ItemStackRef, context: EvaluationContext, evalResult: RecipeEvaluationResult<*, *>): List<ScafallItemStack>

    /**
     * Uses the vanilla remainders or modded/plugin remainders, if available and not ignored.
     */
    interface Default : IngredientRemainder {
        companion object

        /**
         * Specifies which remainders should be ignored.
         *
         * Optional: when omitted, all remainders are used if available.
         */
        val ignore: RemainsIgnoreOptions

    }

    /**
     * Uses a custom remainder and replaces the existing remainders, if not ignored.
     */
    interface Custom : IngredientRemainder {
        companion object

        /**
         * Specifies which remainders should be ignored.
         * The custom remainder will replace those that are **not** ignored.
         *
         * Optional: when omitted, all remainders are replaced.
         */
        val ignore: RemainsIgnoreOptions

        /**
         * The custom remainder to use.
         */
        val remainder: ItemStackRef

    }

}

fun IngredientRemainder.Default.Companion.of(ignore: RemainsIgnoreOptions) =
    CustomCraftingProvider.get().factories.recipeFactory.ingredient.createRemainderDefault(ignore)

fun IngredientRemainder.Custom.Companion.of(ignore: RemainsIgnoreOptions, remainder: ItemStackRef) =
    CustomCraftingProvider.get().factories.recipeFactory.ingredient.createRemainderCustom(ignore, remainder)