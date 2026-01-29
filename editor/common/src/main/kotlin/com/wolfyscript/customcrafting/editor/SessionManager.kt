package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.server.CustomCraftingServer
import java.util.UUID

val CustomCraftingServer.recipeEditor: SessionManager by lazy { SessionManagerImpl() }

/**
 * Manages the creation and editing of recipes.
 */
interface SessionManager {

    /**
     * Retrieves the session for the given player.
     *
     * @return The session if found; null otherwise
     */
    fun getSession(uuid: UUID): EditorSession?

    /**
     * Attempts to create a new session for the given player.
     *
     * @return The session if successful; error otherwise (e.g. player not found, max sessions reached, etc.)
     */
    fun createSession(uuid: UUID): Result<EditorSession>

    fun getOrCreateSession(uuid: UUID): Result<EditorSession>

    fun deleteSession(uuid: UUID)

}