package com.wolfyscript.customcrafting.ui.editor

import androidx.compose.runtime.*
import com.wolfyscript.customcrafting.ui.editor.home.EditorHome
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.RecipeEditor
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.viewProperties
import com.wolfyscript.viewportl.gui.elements.*

@Composable
internal fun RecipeEditorRoot() {
    viewProperties(Key.customCrafting("recipe_editor")) {
        size(9.slots, 4.slots)
        title("<b>Recipe Editor")
    }

    val backstack = remember { mutableStateListOf<NavKey>(Paths.Home) }

    NavigationRoot(backstack) {
        composable<Paths.Home> {
            EditorHome(backstack)
        }

        composable<Paths.RecipeEditor> {
            RecipeEditor(it.recipeType, backstack)
        }

    }

}
