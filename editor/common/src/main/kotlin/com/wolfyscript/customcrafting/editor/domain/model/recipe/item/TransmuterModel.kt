package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier

interface TransmuterModel<T: RecipeItemModifier.Transformation.Transmuter> {

    fun complete(): Result<T>

}