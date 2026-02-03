package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * List of all the default [ResultActions][ResultAction] that exist in the Registry across all platforms.
 */
object ResultActions {

    val command = create<Class<ResultAction>>("command")

    private inline fun <reified T: Class<out ResultAction>> create(key: String) : ValueReference<Class<out ResultAction>, T> {
        return _root_ide_package_.com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes.resultActions.key.referenced<Class<out ResultAction>, T>(
            _root_ide_package_.com.wolfyscript.scafall.identifier.Key.Companion.customCrafting(key)).reference { CustomCraftingProvider.get().registries }
    }

}