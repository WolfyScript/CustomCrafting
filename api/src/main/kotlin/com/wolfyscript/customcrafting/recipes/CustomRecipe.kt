package com.wolfyscript.customcrafting.recipes

interface CustomRecipe<T> {

    val type: RecipeType<T>

    val priority: Int

}