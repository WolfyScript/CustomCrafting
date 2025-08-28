package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface Ingredient {

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
     * @return The matching [ItemStackRef] from the ingredient choices; or null if none match
     */
    fun match(stack: ItemStack): ItemStackRef?

    fun shrink(context: EvaluationContext, ref: ItemStackRef, stack: ItemStack, amount: Int): ItemStack

}