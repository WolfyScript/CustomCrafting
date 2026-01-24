package com.wolfyscript.customcrafting.editor.domain.recipes.recipeitem

import com.wolfyscript.customcrafting.recipes.RecipeItemModifier

interface RecipeItemModifierModel {

    val transformations: List<TransformationModel>

    fun complete(): Result<RecipeItemModifier>

}