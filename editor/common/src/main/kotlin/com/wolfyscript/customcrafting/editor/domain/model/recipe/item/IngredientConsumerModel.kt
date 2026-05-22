package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientConsumer

interface IngredientConsumerModel<T: IngredientConsumer> {

    fun complete(): Result<T>

}