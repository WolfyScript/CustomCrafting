package com.wolfyscript.customcrafting.core.recipe

import net.minecraft.world.item.Item

internal class RecipeTypeImpl<T: CustomRecipe<*,*>>(
    override val recipeClass: Class<T>,
    override val icon: Item
) : RecipeType<T> {

    override fun isInstance(recipe: CustomRecipe<*, *>): Boolean {
        return recipeClass.isInstance(recipe)
    }
}