package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.factories.Factories
import com.wolfyscript.customcrafting.factories.FactoriesCommon
import com.wolfyscript.customcrafting.recipes.RecipeManagerCommon
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistriesCommon
import com.wolfyscript.scafall.ScafallProvider
import io.sentry.Sentry

abstract class CustomCraftingCommon : CustomCrafting {

    // Order of initiation is important, almost everything uses Factories, then the second most used are the Registries
    override val factories: Factories = FactoriesCommon()
    override val registries = CustomCraftingRegistriesCommon()

    init {
        initSentry()
        registries.initRegistries()
    }

    // Then continue with the other setup
    override val recipeManager: RecipeManagerCommon = RecipeManagerCommon(this)

    protected fun initSentry() {
        Sentry.configureScope { scope ->
            scope.setTag("minecraft.version", ScafallProvider.get().server.minecraftServer.serverVersion)
            scope.setTag("platform.type", ScafallProvider.get().platformManager.platformType.name)
        }
    }

}