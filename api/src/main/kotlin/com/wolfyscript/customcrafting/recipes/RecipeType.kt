package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot

interface RecipeType<T: CustomRecipe<*,*>> {

    val recipeClass: Class<T>

    val icon: ItemStackSnapshot

    fun isInstance(recipe: CustomRecipe<*,*>): Boolean

}