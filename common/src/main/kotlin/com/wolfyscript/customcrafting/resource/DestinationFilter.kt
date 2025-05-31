package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.scafall.identifier.Key

class DestinationFilter(
    val customCrafting: CustomCrafting,
    val settings: DestinationSettings.FilterSettings,
) : ResourceLoader.Destination.Filter {

    private val includesFilters = settings.includes?.let { IncludesFilter(it) }
    private val excludesFilters = settings.excludes?.let { IncludesFilter(it) }

    override fun accepts(recipe: CustomRecipe<*, *>): Boolean {
        val key = customCrafting.registries.customRecipes.getKey(recipe) ?: return false
        if (includesFilters != null && !includesFilters.matches(key)) {
            return false
        }
        if (excludesFilters != null && excludesFilters.matches(key)) {
            return false
        }
        return true
    }

    class IncludesFilter(val settings: DestinationSettings.FilterSettings.FilterEntry) {

        private val compiledRegex = settings.regex.map { it.toRegex() }

        fun matches(key: Key): Boolean {
            if (settings.namespaces.contains(key.namespace)) {
                return true
            }

            if (
                settings.paths.any {
                    key.value.startsWith(it) &&
                            key.value.replace(it, "").lastIndexOf("/") == -1
                }
            ) {
                return true
            }

            if (compiledRegex.any { it.matches(key.toString()) }) {
                return true
            }

            return false
        }

    }


}

