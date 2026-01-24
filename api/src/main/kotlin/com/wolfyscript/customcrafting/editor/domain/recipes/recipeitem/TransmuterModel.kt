package com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem

import com.wolfyscript.customcrafting.recipes.RecipeItemModifier

interface TransmuterModel<T: RecipeItemModifier.Transformation.Transmuter> {

    fun complete(): Result<T>

}