package com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem

import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatcher
import com.wolfyscript.scafall.identifier.Key

interface IngredientMatcherModel<T: IngredientMatcher> {

    val type: Class<T>

    val typeKey: Key

    fun complete(): Result<T>

}