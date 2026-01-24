package com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem

import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeChoicesModel
import com.wolfyscript.customcrafting.recipes.RecipeResult

interface ResultModel {

    val choices: RecipeChoicesModel

    val actions: List<ResultActionModel<*>>

    val modifier: RecipeItemModifierModel

    fun complete(): Result<RecipeResult>

}