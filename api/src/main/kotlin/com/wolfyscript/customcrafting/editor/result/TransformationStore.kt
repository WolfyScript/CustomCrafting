package com.wolfyscript.customcrafting.editor.result

import com.wolfyscript.customcrafting.recipes.RecipeItemModifier

interface TransformationStore {

    val transmuter: TransmuterStore<*>

    val ingredients: MutableList<Int>

    fun complete(): Result<List<RecipeItemModifier.Transformation>>
}