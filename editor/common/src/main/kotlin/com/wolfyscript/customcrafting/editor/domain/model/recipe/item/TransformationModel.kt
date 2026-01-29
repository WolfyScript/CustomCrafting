package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.recipes.RecipeItemModifier

interface TransformationModel {

    val transmuter: TransmuterModel<*>

    val ingredients: MutableList<Int>

    fun complete(): Result<RecipeItemModifier.Transformation>
}