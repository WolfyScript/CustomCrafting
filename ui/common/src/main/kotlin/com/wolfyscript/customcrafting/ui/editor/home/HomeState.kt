package com.wolfyscript.customcrafting.ui.editor.home

import com.wolfyscript.customcrafting.core.recipe.RecipeType

data class HomeState(
    val recipeTypes: List<RecipeType<*>>,
)
