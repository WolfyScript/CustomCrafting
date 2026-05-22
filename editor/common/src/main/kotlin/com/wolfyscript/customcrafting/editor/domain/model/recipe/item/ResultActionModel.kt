package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.core.recipe.action.ResultAction

interface ResultActionModel<T: ResultAction> {

    fun complete(): Result<T>

}