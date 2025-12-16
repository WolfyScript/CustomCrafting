package com.wolfyscript.customcrafting.editor.recipe_stores

import com.wolfyscript.customcrafting.editor.RecipeState
import com.wolfyscript.customcrafting.editor.result.ResultState
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking

interface RecipeCookingState : RecipeState.RecipeTypeSpecificState<CustomRecipeCooking> {

    val result: ResultState



}