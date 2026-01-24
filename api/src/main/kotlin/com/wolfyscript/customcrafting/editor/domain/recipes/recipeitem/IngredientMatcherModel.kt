package com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem

import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatcher

interface IngredientMatcherModel<T: IngredientMatcher> {

    fun complete(): Result<T>

}