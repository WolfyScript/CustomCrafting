package com.wolfyscript.customcrafting.editor.result

import com.wolfyscript.customcrafting.recipes.RecipeItemModifier

interface ResultModifierStore {

    val transformations: List<RecipeItemModifier.Transformation>

}