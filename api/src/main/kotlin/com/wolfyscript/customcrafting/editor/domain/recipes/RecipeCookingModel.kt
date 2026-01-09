package com.wolfyscript.customcrafting.editor.domain.recipes

import com.wolfyscript.customcrafting.editor.domain.recipes.result.ResultModel
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking

interface RecipeCookingModel : RecipeModel.RecipeTypeSpecificModel<CustomRecipeCooking> {

    val result: ResultModel



}