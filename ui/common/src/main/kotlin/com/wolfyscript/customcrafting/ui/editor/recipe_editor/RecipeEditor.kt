package com.wolfyscript.customcrafting.ui.editor.recipe_editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.wolfyscript.customcrafting.core.recipes.RecipeType
import com.wolfyscript.customcrafting.core.recipes.RecipeTypes
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.crafting.RecipeCraftingEditor
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.elements.NavKey
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store

@Composable
fun RecipeEditor(recipeType: RecipeType<*>, topBackStack: SnapshotStateList<NavKey>) {

    val recipeStore = store(key = Key.customCrafting("recipe_common")) {
        RecipeStore()
    }

    when (recipeType) {
        RecipeTypes.crafting.resolveOrThrow() -> {
            RecipeCraftingEditor(topBackStack = topBackStack)
        }
    }
}

class RecipeStore : Store()