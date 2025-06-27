package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult
import com.wolfyscript.customcrafting.recipes.data.RecipeInput
import com.wolfyscript.scafall.identifier.Key

interface RecipeManager {

    fun <I: RecipeInput, D: RecipeEvaluationResult.Data, T: CustomRecipe<I,D>> evaluateRecipesOfType(type: RecipeType<T>, input: I, context: EvaluationContext): RecipeEvaluationResult<D,T>?

    fun disableRecipe(recipe: Key)

    fun enableRecipe(key: Key)

    val disabledRecipes: Set<Key>

    fun getRecipe(key: Key): CustomRecipe<*,*>?

    fun removeRecipe(key: Key)

    fun updateRecipe(key: Key, recipe: CustomRecipe<*,*>)

}