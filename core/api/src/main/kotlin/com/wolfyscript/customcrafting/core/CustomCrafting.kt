package com.wolfyscript.customcrafting.core

import com.wolfyscript.customcrafting.core.configuration.ConfigurationManager
import com.wolfyscript.customcrafting.core.factories.Factories
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistries
import com.wolfyscript.customcrafting.core.server.CustomCraftingServer
import com.wolfyscript.scafall.loader.module.Client
import com.wolfyscript.scafall.loader.module.Module
import org.slf4j.Logger

/**
 * The main entry point of the CustomCrafting API.
 * This API is available as soon as the mod/plugin has been instantiated, across both server and client.
 *
 * * [server] The part of the API only available on the Server (Integrated or Dedicated)
 * * [client] The part of the API only available on the Client
 */
interface CustomCrafting : Module<com.wolfyscript.customcrafting.core.server.CustomCraftingServer, Client> {

    val registries: com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistries

    val configurationManager: com.wolfyscript.customcrafting.core.configuration.ConfigurationManager

    /**
     * The factories to instantiate objects with platform specific implementations.
     */
    val factories: com.wolfyscript.customcrafting.core.factories.Factories

    val logger: Logger

}