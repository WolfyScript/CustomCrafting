package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

object ResultActions {

    val command = create<Class<ResultAction>>("command")

    private inline fun <reified T: Class<out ResultAction>> create(key: String) : ValueReference<Class<out ResultAction>, T> {
        return CustomCraftingRegistryTypes.resultActions.key.referenced<Class<out ResultAction>, T>(Key.customCrafting(key)).reference { CustomCraftingProvider.get().registries }
    }

}