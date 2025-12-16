package com.wolfyscript.customcrafting.editor.result

import com.wolfyscript.customcrafting.recipes.ResultAction

interface ResultActionState<T: ResultAction> {

    fun complete(): Result<T>

}