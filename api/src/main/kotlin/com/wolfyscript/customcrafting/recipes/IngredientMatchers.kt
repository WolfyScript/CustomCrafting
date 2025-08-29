package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * List of all the default [IngredientMatchers][IngredientMatcher] that exist in the Registry across all platforms.
 */
object IngredientMatchers {

    val item = create<Class<IngredientMatcher>>("item")
    val exact = create<Class<IngredientMatcher>>("exact")

    private inline fun <reified T: Class<out IngredientMatcher>> create(key: String) : ValueReference<Class<out IngredientMatcher>, T> {
        return CustomCraftingRegistryTypes.ingredientMatchers.key
            .referenced<Class<out IngredientMatcher>, T>(Key.customCrafting(key))
            .reference { CustomCraftingProvider.get().registries }
    }

}