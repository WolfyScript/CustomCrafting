package com.wolfyscript.customcrafting.editor.result

import com.wolfyscript.customcrafting.recipes.ResultModifier

interface TransmuterStore<T: ResultModifier.Transformation.Transmuter> {

    fun complete(): Result<T>

}