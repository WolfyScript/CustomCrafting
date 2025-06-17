package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.RecipeInput

interface CustomRecipeStonecutting : CustomRecipe<RecipeInput.StonecuttingRecipeInput, CustomRecipeStonecutting> {

    override val type: RecipeType<CustomRecipeStonecutting>
        get() = RecipeTypes.stonecutting.resolveOrThrow()

    val source: Ingredient

    val result: RecipeResult

}