package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.core.recipe.RecipeResult

interface ResultModel {

    val choices: RecipeChoicesModel

    val actions: List<ResultActionModel<*>>

    val modifier: RecipeItemModifierModel

    fun complete(): Result<RecipeResult>

}