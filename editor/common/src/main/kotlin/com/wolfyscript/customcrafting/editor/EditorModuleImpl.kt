package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.server.CustomCraftingServer
import com.wolfyscript.scafall.loader.module.Client
import com.wolfyscript.scafall.loader.module.Server

internal class EditorModuleImpl(classLoader: ClassLoader) : EditorModule {

    val registries: EditorRegistries = EditorRegistries()
    override val sessionmanager: SessionManager = SessionManagerImpl()

    override val server: Server? = null
    override val client: Client? = null

    init {
        EditorModule.register(this)
    }

    override fun onInit() {
        CustomCraftingProvider.get().logger.info("[Editor] Initializing Editor Module")
        registries.initRegistries()
    }

    override fun onServerAvailable(fn: (server: Server) -> Unit) {
    }

    override fun onClientAvailable(fn: (client: Client) -> Unit) {}
}

val CustomCraftingServer.recipeEditor: SessionManager
    get() = EditorModule.get().sessionmanager
