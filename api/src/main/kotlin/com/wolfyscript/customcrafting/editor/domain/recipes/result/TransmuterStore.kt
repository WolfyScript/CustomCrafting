package com.wolfyscript.customcrafting.editor.domain.recipes.result

import com.wolfyscript.customcrafting.recipes.RecipeItemModifier

interface TransmuterStore<T: RecipeItemModifier.Transformation.Transmuter> {

    fun complete(): Result<T>

}