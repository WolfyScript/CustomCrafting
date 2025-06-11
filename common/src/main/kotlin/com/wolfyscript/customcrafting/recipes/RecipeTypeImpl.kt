package com.wolfyscript.customcrafting.recipes

class RecipeTypeImpl<T: CustomRecipe<*,*>>(
    override val recipeClass: Class<T>
) : RecipeType<T> {

    override fun isInstance(recipe: CustomRecipe<*, *>): Boolean {
        return recipeClass.isInstance(recipe)
    }
}