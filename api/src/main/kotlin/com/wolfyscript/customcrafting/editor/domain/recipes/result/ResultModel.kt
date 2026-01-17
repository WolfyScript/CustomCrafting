package com.wolfyscript.customcrafting.editor.domain.recipes.result

import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeChoicesModel
import com.wolfyscript.customcrafting.recipes.RecipeResult

interface ResultModel {

    val choices: RecipeChoicesModel

    val actions: List<ResultActionState<*>>

    val modifier: ResultModifierState

    fun complete(): Result<RecipeResult>

}