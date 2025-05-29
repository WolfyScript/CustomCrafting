package com.wolfyscript.customcrafting.editor.conditions

import com.wolfyscript.customcrafting.recipes.RecipeConditions

interface RecipeConditionsStore {

    val conditions: List<ConditionStore<*>>

    fun complete(): Result<RecipeConditions>

}