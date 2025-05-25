package com.wolfyscript.customcrafting.editor.result

import com.wolfyscript.customcrafting.recipes.RecipeResult

interface ResultStore {

    val actions: List<ResultActionStore<*>>

    val modifier: ResultModifierStore

    fun complete(): Result<RecipeResult>

}