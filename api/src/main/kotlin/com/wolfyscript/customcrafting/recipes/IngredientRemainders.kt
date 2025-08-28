package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

object IngredientRemainders {

    val default = create<Class<IngredientRemainder>>("default")
    val custom = create<Class<IngredientRemainder>>("custom")

    private inline fun <reified T: Class<out IngredientRemainder>> create(key: String) : ValueReference<Class<out IngredientRemainder>, T> {
        return CustomCraftingRegistryTypes.ingredientRemainders.key
            .referenced<Class<out IngredientRemainder>, T>(Key.customCrafting(key))
            .reference { CustomCraftingProvider.get().registries }
    }

}