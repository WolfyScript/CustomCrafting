package com.wolfyscript.customcrafting.editor.domain.model.recipe.conditions

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions

interface RecipeConditionsModel {

    val conditions: List<ConditionModel<*>>

    fun complete(): Result<RecipeConditions>

}