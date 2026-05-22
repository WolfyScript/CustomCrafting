package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.core.recipe.condition.Condition
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * A list of all the default [RecipeConditions][com.wolfyscript.customcrafting.core.recipe.condition.Condition] that exist in the Registry across all platforms.
 */
object RecipeConditions {

    private inline fun <reified T: Class<out Condition>> create(key: String) : ValueReference<Class<out Condition>, T> {
        return CustomCraftingRegistryTypes.recipeConditionTypes.key.referenced<Class<out Condition>, T>(
            Key.customCrafting(key)).reference { CustomCraftingProvider.get().registries }
    }

}