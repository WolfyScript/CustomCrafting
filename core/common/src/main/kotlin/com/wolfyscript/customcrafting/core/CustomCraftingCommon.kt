package com.wolfyscript.customcrafting.core

import com.wolfyscript.customcrafting.core.factories.FactoriesCommon
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistriesCommon
import com.wolfyscript.customcrafting.core.factories.Factories
import com.wolfyscript.customcrafting.core.server.CustomCraftingServer
import com.wolfyscript.scafall.loader.module.BasicModule
import com.wolfyscript.scafall.loader.module.Client

abstract class CustomCraftingCommon : BasicModule<CustomCraftingServer, Client>(), CustomCrafting {

    // Order of initiation is important, almost everything uses Factories, then the second most used are the Registries
    override val factories: Factories = FactoriesCommon()
    override val registries = CustomCraftingRegistriesCommon()

    init {
        registries.initRegistries()
    }

}