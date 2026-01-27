package com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem

import com.wolfyscript.customcrafting.recipes.ingredient.IngredientConsumer

interface IngredientConsumerModel<T: IngredientConsumer> {

    fun complete(): Result<T>

}