package com.wolfyscript.customcrafting.editor.domain.model.recipe.item

import com.wolfyscript.customcrafting.core.recipe.modifier.RecipeItemModifier

interface RecipeItemModifierModel {

    val transformations: List<TransformationModel>

    fun complete(): Result<RecipeItemModifier>

}