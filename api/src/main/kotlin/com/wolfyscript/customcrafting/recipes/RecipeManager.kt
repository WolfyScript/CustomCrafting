package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.recipes.data.CraftingMatrixData
import com.wolfyscript.customcrafting.recipes.data.RecipeData

interface RecipeManager {

    fun evaluateCraftingRecipes(matrix: CraftingMatrixData): RecipeData<CustomRecipeCrafting>?

}