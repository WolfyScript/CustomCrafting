package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.world.Location
import com.wolfyscript.scafall.wrappers.world.entity.Player
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface RecipeResult {

    val stacks: List<ItemStackRef>
    val tags: List<Key>

    val modifier: ResultModifier

    val actions: List<ResultAction>

    val bulkActions: List<ResultAction>

    /**
     * Evaluates the result of the recipe based on the cached [RecipeData].
     *
     * If already evaluated, this simply returns the previously evaluated value.
     */
    fun evaluate(recipeData: RecipeData<*>, player: Player?, location: Location?): ItemStack

}