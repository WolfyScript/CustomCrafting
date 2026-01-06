package com.wolfyscript.customcrafting.editor.model.recipes

import com.wolfyscript.customcrafting.editor.model.recipes.result.ResultModel
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking

interface RecipeCookingModel : RecipeModel.RecipeTypeSpecificModel<CustomRecipeCooking> {

    val result: ResultModel



}