package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.core.recipes.RecipeItemModifier

interface RecipeItemModifierModel {

    val transformations: List<TransformationModel>

    fun complete(): Result<RecipeItemModifier>

}