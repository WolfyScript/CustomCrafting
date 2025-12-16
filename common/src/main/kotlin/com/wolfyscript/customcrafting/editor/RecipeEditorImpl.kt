package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.server.CustomCraftingServer
import java.util.UUID

// TODO: should this be accessible from outside? for now just have it internal
internal val CustomCraftingServer.recipeEditor: RecipeEditor by lazy { RecipeEditorImpl() }

class RecipeEditorImpl : RecipeEditor {

    private val sessions: MutableMap<UUID, EditorSession> = mutableMapOf()

    override fun getSession(uuid: UUID): EditorSession? {
        return sessions[uuid]
    }

    override fun createSession(uuid: UUID): Result<EditorSession> {
        val session = EditorSessionImpl(uuid)
        sessions[uuid] = session
        return Result.success(session)
    }

    override fun getOrCreateSession(uuid: UUID): Result<EditorSession> {
        if (sessions.containsKey(uuid)) {
            return Result.success(sessions[uuid]!!)
        }
        return createSession(uuid)
    }

    override fun deleteSession(uuid: UUID) {
        sessions.remove(uuid)
    }

}