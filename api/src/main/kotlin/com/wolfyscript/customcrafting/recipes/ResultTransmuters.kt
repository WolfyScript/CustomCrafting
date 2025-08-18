package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * List of all the default [Transmuters][ResultModifier.Transformation.Transmuter] that exist in the Registry across all platforms.
 */
object ResultTransmuters {

    private inline fun <reified T: Class<out ResultModifier.Transformation.Transmuter>> create(key: String) : ValueReference<Class<out ResultModifier.Transformation.Transmuter>, T> {
        return CustomCraftingRegistryTypes.resultTransmuters.key.referenced<Class<out ResultModifier.Transformation.Transmuter>, T>(Key.customCrafting(key)).reference { CustomCraftingProvider.get().registries }
    }

}