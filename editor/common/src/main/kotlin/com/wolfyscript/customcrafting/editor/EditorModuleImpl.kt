package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.server.CustomCraftingServer
import com.wolfyscript.customcrafting.editor.cli.commands.RecipesEditorCommand
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.loader.module.BasicModule
import com.wolfyscript.scafall.loader.module.Client

internal class EditorModuleImpl(classLoader: ClassLoader) : BasicModule<EditorServer, Client>(), EditorModule {

    override val registries: EditorRegistries = EditorRegistries()

    init {
        EditorModule.register(this)
    }

    override fun onInit() {
        CustomCraftingProvider.get().logger.info("[Editor] Initializing Editor Module")
        registries.initRegistries()
        server = EditorServerImpl()

        ScafallProvider.get().server?.minecraftServer?.commands?.dispatcher?.let {
            RecipesEditorCommand.register(it)
        }
    }

}

val CustomCraftingServer.recipeEditor: SessionManager
    get() = EditorModule.get().server?.sessionManager ?: error("CustomCrafting server not initialized")
