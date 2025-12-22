package com.wolfyscript.customcrafting.editor.model.recipes.conditions

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions

interface RecipeConditionsStore {

    val conditions: List<ConditionStore<*>>

    fun complete(): Result<RecipeConditions>

}