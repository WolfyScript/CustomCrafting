package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface Ingredient {

    val choices: RecipeChoices

    val replaceWithRemains: Boolean

    /**
     * Matches this ingredient against the given stack.
     *
     * @return The matching [ItemStackRef] from the ingredient choices; or null if none match
     */
    fun match(stack: ItemStack, exact: Boolean): ItemStackRef?

}