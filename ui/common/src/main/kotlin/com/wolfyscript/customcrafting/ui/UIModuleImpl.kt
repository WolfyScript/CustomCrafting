package com.wolfyscript.customcrafting.ui

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.scafall.loader.module.Client
import com.wolfyscript.scafall.loader.module.Server

internal class UIModuleImpl(classLoader: ClassLoader) : UIModule {

    override val server: Server? = null
    override val client: Client? = null

    init {
        UIModule.register(this)
    }

    override fun onInit() {
        CustomCraftingProvider.get().logger.info("[UI] Initializing UI Module")

    }

    override fun onServerAvailable(fn: (server: Server) -> Unit) {}

    override fun onClientAvailable(fn: (client: Client) -> Unit) {}
}