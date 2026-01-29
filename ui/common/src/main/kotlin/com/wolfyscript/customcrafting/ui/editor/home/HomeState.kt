package com.wolfyscript.customcrafting.ui.editor.home

import com.wolfyscript.customcrafting.recipes.RecipeType

data class HomeState(
    val recipeTypes: List<RecipeType<*>>,
)
