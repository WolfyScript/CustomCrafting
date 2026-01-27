package com.wolfyscript.customcrafting.editor.ui

import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.viewportl.gui.elements.NavKey

interface Paths {

    object Home : NavKey
    class RecipeEditor(val recipeType: RecipeType<*>) : NavKey
    class IngredientEditor() : NavKey

}