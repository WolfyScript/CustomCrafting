package com.wolfyscript.customcrafting.recipes.data

import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.customcrafting.recipes.RecipeResult

interface RecipeData<T: CustomRecipe<*>> {

    val recipe: T

    var result: RecipeResult

    fun bySlot(slot: Int): IngredientData?

    fun nonNullIngredients(): List<IngredientData>

}