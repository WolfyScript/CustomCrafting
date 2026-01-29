package com.wolfyscript.customcrafting.editor

import java.util.UUID

internal class SessionManagerImpl : SessionManager {

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