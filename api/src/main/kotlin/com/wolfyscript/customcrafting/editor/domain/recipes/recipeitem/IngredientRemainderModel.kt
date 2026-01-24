package com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem

import com.wolfyscript.customcrafting.recipes.ingredient.IngredientRemainder

interface IngredientRemainderModel<T: IngredientRemainder> {

    fun complete(): Result<T>

}