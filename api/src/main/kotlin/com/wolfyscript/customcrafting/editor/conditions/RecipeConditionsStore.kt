package com.wolfyscript.customcrafting.editor.conditions

import com.wolfyscript.customcrafting.recipes.conditions.RecipeConditions

interface RecipeConditionsStore {

    val conditions: List<ConditionStore<*>>

    fun complete(): Result<RecipeConditions>

}