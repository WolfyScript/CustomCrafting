package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.core.recipe.modifier.Transformation

interface TransformationModel {

    val transmuter: TransmuterModel<*>

    val ingredients: MutableList<Int>

    fun complete(): Result<Transformation>
}