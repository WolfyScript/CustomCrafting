package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot

class RecipeTypeImpl<T: CustomRecipe<*,*>>(
    override val recipeClass: Class<T>,
    override val icon: ItemStackSnapshot
) : RecipeType<T> {

    override fun isInstance(recipe: CustomRecipe<*, *>): Boolean {
        return recipeClass.isInstance(recipe)
    }
}