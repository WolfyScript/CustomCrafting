package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef

/**
 * A list of ItemStacks and/or Tags that an [com.wolfyscript.customcrafting.recipes.ingredient.Ingredient] accepts or a [RecipeResult] can choose from.
 */
interface RecipeChoices {

    companion object {
        fun of(stacks: List<ItemStackRef>, tags: List<Key> = emptyList()) =
            CustomCraftingProvider.get().factories.recipeFactory.recipeItem.createChoices(stacks, tags)
    }

    /**
     * The stacks of this choice
     */
    val stacks: List<ItemStackRef>

    /**
     * The keys of the tags
     */
    val tags: List<Key>

    /**
     * Combines all the stacks and tags into a single list.
     * This converts the tags into a list of stacks they contain.
     */
    fun all(): List<ItemStackRef>

    @Deprecated("Unsure if this is actually needed. Use all() instead for now.")
    fun allFor(context: EvaluationContext): List<ItemStackRef>

}