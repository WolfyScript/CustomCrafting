package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.server.CustomCraftingServer
import com.wolfyscript.scafall.loader.module.BasicModule
import com.wolfyscript.scafall.loader.module.Client
import com.wolfyscript.scafall.loader.module.Server

internal class EditorModuleImpl(classLoader: ClassLoader) : BasicModule<EditorServer, Client>(), EditorModule {

    val registries: EditorRegistries = EditorRegistries()

    init {
        EditorModule.register(this)
    }

    override fun onInit() {
        CustomCraftingProvider.get().logger.info("[Editor] Initializing Editor Module")
        registries.initRegistries()
        server = EditorServerImpl()
    }

}

val CustomCraftingServer.recipeEditor: SessionManager
    get() = EditorModule.get().server?.sessionManager ?: error("CustomCrafting server not initialized")
