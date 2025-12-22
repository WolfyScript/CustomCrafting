package com.wolfyscript.customcrafting.editor.model.recipes.result

import com.wolfyscript.customcrafting.recipes.RecipeItemModifier

interface ResultModifierState {

    val transformations: List<RecipeItemModifier.Transformation>

}