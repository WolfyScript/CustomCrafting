package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings
import com.wolfyscript.customcrafting.recipes.CustomRecipe

class DestinationFilter(val customCrafting: CustomCrafting, val resourceLoaderImpl: ResourceLoaderImpl, val settings: DestinationSettings.FilterSettings) :
    ResourceLoader.Destination.Filter {

    override fun accepts(recipe: CustomRecipe): Boolean {
        val key = customCrafting.registries.customRecipes.getKey(recipe) ?: return false
        if (settings.excludeNamespaces.contains(key.namespace)) return false
        if (settings.includeNamespaces.isNotEmpty() && !settings.includeNamespaces.contains(key.namespace)) return false
        return true
    }

}

