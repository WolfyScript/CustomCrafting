package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.editor.model.SessionModel
import com.wolfyscript.customcrafting.recipes.RecipeType
import com.wolfyscript.scafall.identifier.Key
import java.util.UUID

/**
 * A session, bound to a player, to edit or create recipes.
 */
interface EditorSession {

    val user: UUID

    val state: SessionModel?

    /**
     * Starts editing an existing recipe.
     *
     * @return A Result containing the new mode if successful; or an exception if not (e.g. doesn't exist, already being edited, no permission, etc.)
     */
    fun edit(recipeKey: Key) : Result<SessionModel>

    /**
     * Starts creating a new recipe of the given type.
     *
     * @return A Result containing the new mode if successful; or an exception if not (e.g. invalid recipe type, no permission, etc.)
     */
    fun create(recipeType: RecipeType<*>) : Result<SessionModel>

    /**
     * Cancels and resets the current state.
     */
    fun cancel()

}