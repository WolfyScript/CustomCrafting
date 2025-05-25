package com.wolfyscript.customcrafting.editor.result

import com.wolfyscript.customcrafting.recipes.ResultModifier

interface ResultModifierStore {

    val transformations: List<ResultModifier.Transformation>

}