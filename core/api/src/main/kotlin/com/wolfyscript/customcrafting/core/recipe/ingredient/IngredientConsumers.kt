package com.wolfyscript.customcrafting.core.recipe.ingredient

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * List of all the default [IngredientConsumers][IngredientConsumer] that exist in the Registry across all platforms.
 */
object IngredientConsumers {

    val consume = create<Class<IngredientConsumer>>("consume")
    val replace = create<Class<IngredientConsumer>>("replace")
    val keep = create<Class<IngredientConsumer>>("keep")

    private inline fun <reified T: Class<out IngredientConsumer>> create(key: String) : ValueReference<Class<out IngredientConsumer>, T> {
        return CustomCraftingRegistryTypes.ingredientConsumers.key
            .referenced<Class<out IngredientConsumer>, T>(Key.customCrafting(key))
            .reference { CustomCraftingProvider.get().registries }
    }

}