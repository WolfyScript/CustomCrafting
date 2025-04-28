package com.wolfyscript.customcrafting.recipes

interface RecipeType<RT> {

    val type: Class<RT>

    fun isInstance(recipe: CustomRecipe<*>): Boolean

}