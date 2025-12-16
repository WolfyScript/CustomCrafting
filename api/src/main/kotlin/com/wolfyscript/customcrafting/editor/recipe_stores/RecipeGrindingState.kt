package com.wolfyscript.customcrafting.editor.recipe_stores

import com.wolfyscript.customcrafting.editor.RecipeState
import com.wolfyscript.customcrafting.editor.result.ResultState
import com.wolfyscript.customcrafting.recipes.CustomRecipeGrinding

interface RecipeGrindingState : RecipeState.RecipeTypeSpecificState<CustomRecipeGrinding> {

    val result: ResultState

    var xp: Int

}