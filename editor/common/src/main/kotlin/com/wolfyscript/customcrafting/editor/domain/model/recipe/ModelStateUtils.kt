package com.wolfyscript.customcrafting.editor.domain.model.recipe

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.EditorSession
import com.wolfyscript.customcrafting.editor.recipeEditor
import java.util.*

fun <T> withEditorSession(viewer: UUID, fn: (EditorSession) -> T): T {
    val session = CustomCraftingProvider.get().server?.recipeEditor?.getSession(viewer)
    if (session != null) {
        return fn(session)
    }
    error("Failed to fetch data from session: Session not available")
}

internal fun <T> withCraftingModel(viewer: UUID, fn: (RecipeCraftingModel) -> T): T =
    withEditorSession(viewer) { session ->
        val state = session.model?.recipeModel?.recipeTypeSpecificModel as? RecipeCraftingModel
            ?: error("Expected RecipeCraftingState, but was ${session.model?.recipeModel?.recipeTypeSpecificModel}")
        return@withEditorSession fn(state)
    }

internal fun <T> withCraftingModel(session: EditorSession, fn: (RecipeCraftingModel) -> T): T {
    val state = session.model?.recipeModel?.recipeTypeSpecificModel as? RecipeCraftingModel
        ?: error("Expected RecipeCraftingState, but was ${session.model?.recipeModel?.recipeTypeSpecificModel}")
    return fn(state)
}
