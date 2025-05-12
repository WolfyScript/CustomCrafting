package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface CustomRecipeStonecutting : CustomRecipe<CustomRecipeStonecutting> {

    val source: Ingredient

    val result: RecipeResult

    fun evaluate(context: EvaluationContext, stack: ItemStack): RecipeData<CustomRecipeStonecutting>?

}