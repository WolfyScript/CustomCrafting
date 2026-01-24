package com.wolfyscript.customcrafting.editor.domain.recipes

import com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem.ResultModel
import com.wolfyscript.customcrafting.recipes.CustomRecipeGrinding

interface RecipeGrindingModel : RecipeModel.RecipeTypeSpecificModel<CustomRecipeGrinding> {

    val result: ResultModel

    var xp: Int

}