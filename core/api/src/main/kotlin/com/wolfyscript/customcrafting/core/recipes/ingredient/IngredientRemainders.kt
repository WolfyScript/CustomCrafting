package com.wolfyscript.customcrafting.core.recipes.ingredient

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * List of all the default [IngredientRemainders][IngredientRemainder] that exist in the Registry across all platforms.
 */
object IngredientRemainders {

    val default = create<Class<IngredientRemainder>>("default")
    val custom = create<Class<IngredientRemainder>>("custom")

    private inline fun <reified T: Class<out IngredientRemainder>> create(key: String) : ValueReference<Class<out IngredientRemainder>, T> {
        return _root_ide_package_.com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes.ingredientRemainders.key
            .referenced<Class<out IngredientRemainder>, T>(_root_ide_package_.com.wolfyscript.scafall.identifier.Key.Companion.customCrafting(key))
            .reference { CustomCraftingProvider.get().registries }
    }

}