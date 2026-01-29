package com.wolfyscript.customcrafting.ui.editor

import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.viewportl.gui.elements.NavKey

interface Paths {

    object Home : NavKey
    class RecipeEditor(val recipeType: RecipeType<*>) : NavKey
    class IngredientEditor() : NavKey

}