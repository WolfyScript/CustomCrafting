package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.editor.model.recipes.result.ResultModel
import com.wolfyscript.customcrafting.recipes.CustomRecipeGrinding

interface RecipeGrindingModel : RecipeModel.RecipeTypeSpecificModel<CustomRecipeGrinding> {

    val result: ResultModel

    var xp: Int

}