package com.wolfyscript.customcrafting.ui.editor.home

import com.wolfyscript.customcrafting.core.recipes.RecipeType

data class HomeState(
    val recipeTypes: List<RecipeType<*>>,
)
