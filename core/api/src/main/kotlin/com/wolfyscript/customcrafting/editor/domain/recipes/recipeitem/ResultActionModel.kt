package com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem

import com.wolfyscript.customcrafting.recipes.ResultAction

interface ResultActionModel<T: ResultAction> {

    fun complete(): Result<T>

}