package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.identifier.Key

interface RecipeManager {

    fun <I: RecipeInput, T: CustomRecipe<I,T>> evaluateRecipesOfType(type: RecipeType<T>, input: I, context: EvaluationContext): RecipeData<T>?

    fun disableRecipe(recipe: CustomRecipe<*,*>)

    fun enableRecipe(key: Key)

    val disabledRecipes: Set<Key>

    fun getRecipe(key: Key): CustomRecipe<*,*>?

    fun removeRecipe(key: Key)

    fun updateRecipe(key: Key, recipe: CustomRecipe<*,*>)

}