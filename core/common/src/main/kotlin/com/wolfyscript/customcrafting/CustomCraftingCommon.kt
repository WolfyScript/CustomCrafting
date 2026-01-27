package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.factories.Factories
import com.wolfyscript.customcrafting.factories.FactoriesCommon
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistriesCommon
import com.wolfyscript.customcrafting.server.CustomCraftingServer
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.loader.module.Client
import com.wolfyscript.scafall.loader.module.Server
import com.wolfyscript.scafall.server.ScafallServer
import io.sentry.Sentry

abstract class CustomCraftingCommon : CustomCrafting {

    // Order of initiation is important, almost everything uses Factories, then the second most used are the Registries
    override val factories: Factories = FactoriesCommon()
    override val registries = CustomCraftingRegistriesCommon()
    override var client = null
        set(value) {
            field = value
            if (value != null) {
                clientListeners.forEach { it(value) }
                clientListeners.clear()
            }
        }

    private val serverListeners: MutableList<(CustomCraftingServer) -> Unit> = mutableListOf()
    private val clientListeners: MutableList<(Client) -> Unit> = mutableListOf()

    override var server: CustomCraftingServer? = null
        set(value) {
            field = value
            if (value != null) {
                serverListeners.forEach { it(value) }
                serverListeners.clear()
            }
        }


    override fun onServerAvailable(fn: (server: CustomCraftingServer) -> Unit) {
        if (server == null) {
            serverListeners.add(fn)
        } else {
            fn(server!!)
        }
    }

    override fun onClientAvailable(fn: (client: Client) -> Unit) {
        if (client == null) {
            clientListeners.add(fn)
        } else {
            fn(client!!)
        }
    }

    init {
        initSentry()
        registries.initRegistries()
    }

    protected fun initSentry() {
        Sentry.configureScope { scope ->
            scope.setTag("minecraft.version", ScafallProvider.get().server?.minecraftServer?.serverVersion ?: "unknown")
            scope.setTag("platform.type", ScafallProvider.get().platformManager.platformType.name)
        }
    }

}