package com.wolfyscript.customcrafting.editor.model.recipes.result

import com.wolfyscript.customcrafting.recipes.ResultAction

interface ResultActionState<T: ResultAction> {

    fun complete(): Result<T>

}