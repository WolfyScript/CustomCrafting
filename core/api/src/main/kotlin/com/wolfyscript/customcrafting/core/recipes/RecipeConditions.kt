package com.wolfyscript.customcrafting.core.recipes

import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.core.recipes.conditions.Condition
import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.ValueReference
import com.wolfyscript.scafall.registry.referenced

/**
 * A list of all the default [RecipeConditions][com.wolfyscript.customcrafting.core.recipes.conditions.Condition] that exist in the Registry across all platforms.
 */
object RecipeConditions {

    private inline fun <reified T: Class<out com.wolfyscript.customcrafting.core.recipes.conditions.Condition>> create(key: String) : ValueReference<Class<out com.wolfyscript.customcrafting.core.recipes.conditions.Condition>, T> {
        return _root_ide_package_.com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistryTypes.recipeConditionTypes.key.referenced<Class<out com.wolfyscript.customcrafting.core.recipes.conditions.Condition>, T>(
            _root_ide_package_.com.wolfyscript.scafall.identifier.Key.Companion.customCrafting(key)).reference { CustomCraftingProvider.get().registries }
    }

}