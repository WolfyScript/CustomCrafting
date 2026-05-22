package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.recipe.modifier.Transformation
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * List of all the default [Transmuters][com.wolfyscript.customcrafting.core.recipe.modifier.Transformation.Transmuter] that exist in the Registry across all platforms.
 */
object RecipeItemTransmuters {

    private inline fun <reified T: Class<out Transformation.Transmuter>> create(key: String) : ValueReference<Class<out Transformation.Transmuter>, T> {
        return CustomCraftingRegistryTypes.recipeItemTransmuters.key.referenced<Class<out Transformation.Transmuter>, T>(
            Key.customCrafting(key)).reference { CustomCraftingProvider.get().registries }
    }

}