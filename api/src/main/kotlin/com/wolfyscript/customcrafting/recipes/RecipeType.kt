package com.wolfyscript.customcrafting.recipes

interface RecipeType<T: CustomRecipe<*,*>> {

    val type: Class<T>

    val recipeClass: Class<T>

    fun isInstance(recipe: CustomRecipe<*,*>): Boolean

}