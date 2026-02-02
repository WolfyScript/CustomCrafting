package com.wolfyscript.customcrafting.editor

import com.wolfyscript.scafall.loader.module.Server

interface EditorServer : Server {

    val sessionManager: SessionManager

}

internal class EditorServerImpl : EditorServer {

    override val sessionManager: SessionManager = SessionManagerImpl()

    override fun onLoad() {

    }

    override fun onUnload() {

    }
}