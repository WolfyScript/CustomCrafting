package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.core.recipe.modifier.Transformation

interface TransmuterModel<T: Transformation.Transmuter> {

    fun complete(): Result<T>

}