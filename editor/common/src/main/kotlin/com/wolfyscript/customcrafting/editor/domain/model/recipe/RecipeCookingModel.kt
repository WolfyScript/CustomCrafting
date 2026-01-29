package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ResultModel
import com.wolfyscript.customcrafting.recipes.CustomRecipeCooking

interface RecipeCookingModel : RecipeModel.RecipeTypeSpecificModel<CustomRecipeCooking> {

    val result: ResultModel



}