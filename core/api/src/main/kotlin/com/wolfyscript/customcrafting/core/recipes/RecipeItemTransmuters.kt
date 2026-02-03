package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * List of all the default [Transmuters][RecipeItemModifier.Transformation.Transmuter] that exist in the Registry across all platforms.
 */
object RecipeItemTransmuters {

    private inline fun <reified T: Class<out RecipeItemModifier.Transformation.Transmuter>> create(key: String) : ValueReference<Class<out RecipeItemModifier.Transformation.Transmuter>, T> {
        return _root_ide_package_.com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes.recipeItemTransmuters.key.referenced<Class<out RecipeItemModifier.Transformation.Transmuter>, T>(
            _root_ide_package_.com.wolfyscript.scafall.identifier.Key.Companion.customCrafting(key)).reference { CustomCraftingProvider.get().registries }
    }

}