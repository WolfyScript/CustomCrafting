package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext
import com.wolfyscript.customcrafting.core.recipe.RecipeChoices
import com.wolfyscript.customcrafting.core.recipe.RemainsIgnoreOptions
import com.wolfyscript.customcrafting.core.recipe.evaluation.RecipeEvaluationResult
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack

@JsonDeserialize(`as` = IngredientImpl::class)
interface Ingredient {

    companion object {

        fun of(
            choices: RecipeChoices,
            matching: IngredientMatcher = IngredientMatcher.Exact.of(),
            consumption: IngredientConsumer = IngredientConsumer.Consume.of(
                IngredientRemainder.Default.of(
                    RemainsIgnoreOptions.of()
                )
            ),
        ): Ingredient = IngredientImpl(choices, matching, consumption)

    }

    /**
     * The items that can be used to fulfill this ingredient.
     */
    val choices: RecipeChoices

    /**
     * Specifies how the ingredient is matched against the item stacks in the inventory slots.
     *
     * Default: Checks if all components match.
     */
    val matching: IngredientMatcher

    /**
     * Specifies how the ingredient is consumed from the inventory slots.
     *
     * Default: [IngredientConsumer.Consume]
     */
    val consumption: IngredientConsumer

    /**
     * Matches this ingredient against the given stack.
     *
     * @return The matching [com.wolfyscript.scafall.items.ItemStackRef] from the ingredient choices; or null if none match
     */
    fun match(stack: ScafallItemStack): ItemStackRef?

    /**
     * Consumes the ingredient from the given target stack.
     *
     * @param target the target stack to consume.
     * @param count how many instances of the ingredients are to be consumed (Spigot/Paper CC may bulk consume the recipe. On Fabric recipes are consumed one-by-one)
     * @param ref the reference associated with the target.
     * @param context the evaluation context (e.g. player, tile entity, etc.).
     * @param evalResult the result of the recipe evaluation (ingredient info like which ingredient is present in each slot).
     *
     * @return the updated/new [ScafallItemStack] after consumption.
     */
    fun shrink(
        target: ScafallItemStack,
        count: Int,
        ref: ItemStackRef,
        context: EvaluationContext,
        evalResult: RecipeEvaluationResult<*, *>,
    ): ScafallItemStack
}