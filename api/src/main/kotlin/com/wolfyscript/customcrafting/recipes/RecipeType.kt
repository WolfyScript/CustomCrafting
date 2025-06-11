package com.wolfyscript.customcrafting.recipes

interface RecipeType<T: CustomRecipe<*,*>> {

    val recipeClass: Class<T>

    fun isInstance(recipe: CustomRecipe<*,*>): Boolean

}