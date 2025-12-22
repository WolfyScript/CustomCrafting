package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.editor.model.recipes.RecipeState
import com.wolfyscript.customcrafting.editor.model.recipes.result.ResultState
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking

interface RecipeCookingState : RecipeState.RecipeTypeSpecificState<CustomRecipeCooking> {

    val result: ResultState



}