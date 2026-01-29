package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ResultModel
import com.wolfyscript.customcrafting.recipes.CustomRecipeGrinding

interface RecipeGrindingModel : RecipeModel.RecipeTypeSpecificModel<CustomRecipeGrinding> {

    val result: ResultModel

    var xp: Int

}