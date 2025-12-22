package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.editor.model.recipes.RecipeState
import com.wolfyscript.customcrafting.editor.model.recipes.result.ResultState
import com.wolfyscript.customcrafting.recipes.CustomRecipeGrinding

interface RecipeGrindingState : RecipeState.RecipeTypeSpecificState<CustomRecipeGrinding> {

    val result: ResultState

    var xp: Int

}