package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.recipes.ingredient.IngredientRemainder

interface IngredientRemainderModel<T: IngredientRemainder> {

    fun complete(): Result<T>

}