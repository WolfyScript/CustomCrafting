package com.wolfyscript.customcrafting.resource

import com.wolfyscript.customcrafting.configuration.resources.DestinationSettings
import com.wolfyscript.customcrafting.recipes.CustomRecipe
import com.wolfyscript.scafall.identifier.Key

/**
 * The destination to load or save resources from/to.
 *
 * See [DestinationSettings][com.wolfyscript.customcrafting.configuration.resources.DestinationSettings] for configuration.
 */
interface Destination {

    val filter: Filter?

    val settings: DestinationSettings

    fun load(accept: (recipe: LoadedRecipe) -> Unit)

    /**
     * Tries to save the recipe to this destination.
     *
     * @return A Result of whether the recipe was stored; or an exception when an error occurred.
     */
    fun save(key: Key, recipe: CustomRecipe<*, *>): Result<Boolean>

    /**
     * Tries to delete the recipe from this destination.
     *
     * @return A Result of whether the recipe was deleted; or an exception when an error occurred.
     */
    fun delete(key: Key, recipe: CustomRecipe<*, *>): Result<Boolean>

    interface Filter {

        fun accepts(key: Key, recipe: CustomRecipe<*, *>) : Boolean

    }

}