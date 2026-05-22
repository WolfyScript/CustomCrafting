package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.recipe.action.ResultAction
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * List of all the default [ResultActions][com.wolfyscript.customcrafting.core.recipe.action.ResultAction] that exist in the Registry across all platforms.
 */
object ResultActions {

    val command = create<Class<ResultAction>>("command")

    private inline fun <reified T: Class<out ResultAction>> create(key: String) : ValueReference<Class<out ResultAction>, T> {
        return CustomCraftingRegistryTypes.resultActions.key.referenced<Class<out ResultAction>, T>(
            Key.customCrafting(key)).reference { CustomCraftingProvider.get().registries }
    }

}