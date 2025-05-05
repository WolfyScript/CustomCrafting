package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.Location
import com.wolfyscript.scafall.wrappers.world.entity.Player
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

/**
 * The result of a recipe with modifiers and actions.
 */
interface RecipeResult {

    val stacks: List<ItemStackRef>
    val tags: List<Key>

    /**
     * The modifier applied to the result of the recipe.
     */
    val modifier: ResultModifier

    /**
     * The actions, to perform after the recipe is completed.
     */
    val actions: List<ResultAction>

    /**
     * The bulk actions, to perform after the recipe is completed in bulk (e.g. crafting a stack of items using shift-click).
     */
    val bulkActions: List<ResultAction>

    /**
     * Computes the result of the recipe based on the cached [RecipeData].
     *
     * If already computed, this simply returns the previously computed value.
     */
    fun computeOrGet(recipeData: RecipeData<*>, player: Player?, location: Location?): ItemStack

}