package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.scafall.wrappers.world.items.ItemStack

interface CustomRecipeStonecutting : CustomRecipe {

    override val type: RecipeType<CustomRecipeStonecutting>
        get() = RecipeTypes.stonecutting

    val source: Ingredient

    val result: RecipeResult

    fun evaluate(context: EvaluationContext, stack: ItemStack): RecipeData<CustomRecipeStonecutting>?

}