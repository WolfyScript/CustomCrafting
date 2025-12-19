package com.wolfyscript.customcrafting.editor.ui.home

import com.wolfyscript.customcrafting.recipes.RecipeType

data class HomeState(
    val recipeTypes: List<RecipeType<*>>,
)
