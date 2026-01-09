package com.wolfyscript.customcrafting.editor.ui

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.EditorSession
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeCraftingModel
import com.wolfyscript.customcrafting.editor.recipeEditor
import java.util.*

internal fun <T> withEditorSession(viewer: UUID, fn: (EditorSession) -> T): T {
    val session = CustomCraftingProvider.get().server?.recipeEditor?.getSession(viewer)
    if (session != null) {
        return fn(session)
    }
    error("Failed to fetch data from session: Session not available")
}

internal fun <T> withCraftingState(viewer: UUID, fn: (RecipeCraftingModel) -> T): T =
    withEditorSession(viewer) { session ->
        val state = session.state?.recipeModel?.recipeTypeSpecificModel as? RecipeCraftingModel
            ?: error("Expected RecipeCraftingState, but was ${session.state?.recipeModel?.recipeTypeSpecificModel}")
        return@withEditorSession fn(state)
    }

